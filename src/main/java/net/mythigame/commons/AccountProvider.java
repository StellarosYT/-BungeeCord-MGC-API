package net.mythigame.commons;

import net.mythigame.commons.Exceptions.AccountNotFoundException;
import net.mythigame.commons.Storage.MySQL.MySQLManager;
import net.mythigame.commons.Storage.Redis.RedisAccess;
import net.mythigame.commons.Utils.MojangAPI;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountProvider {
    public final String REDIS_KEY = "account:";
    public static final String ACCOUNT_TABLE = "accounts";
    public static final String ACCOUNT_CASE_TABLE = "players_cases";
    public static final Account DEFAULT_ACCOUNT = new Account(UUID.randomUUID(), null, null, RankUnit.JOUEUR, -1, 0, 1, 0, null, null, false, null, false, null, false, false, false, false, false, null);

    private final RedisAccess redisAccess;
    private final UUID uuid;

    public AccountProvider(UUID uuid){
        this.uuid = uuid;
        this.redisAccess = RedisAccess.INSTANCE;
    }

    public Account getAccount(){
        Account account = getAccountFromRedis();

        if(account == null){
            account = getAccountFromMySQL();
            if(account == null){
                account = createNewAccount(uuid);
            }
            updateRedisAccount(account);
        }
        return account;
    }

    public Account getAccountOnLogin() throws AccountNotFoundException {
        Account account = getAccountFromRedis();

        if(account == null){
            account = getAccountFromMySQL();
            if(account == null){
                account = createNewAccount(uuid);
            }
            updateRedisAccount(account);
        }
        return account;
    }

    public void updateRedisAccount(Account account){
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        final String key = REDIS_KEY + uuid;
        final RBucket<Account> accountRBucket = redissonClient.getBucket(key);
        accountRBucket.set(account);
    }

    public void removeFromRedis(){
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        final String key = REDIS_KEY + uuid;
        final RBucket<Account> accountRBucket = redissonClient.getBucket(key);
        accountRBucket.deleteAsync();
    }

    public void updateMySQLAccount(Account account){
        final Connection connection;
        try {
            connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE "+ ACCOUNT_TABLE + " SET username = ?, nickname = ?, grade = ?, grade_end = ?, coins = ?, level = ?, xp = ?, guild = ?, isBanned = ?, ban_id = ?, isMuted = ?, mute_id = ?, isModeration = ?, isNicked = ?, isConnected = ? WHERE uuid = ?");
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getNickname());
            preparedStatement.setString(3, account.getGrade().getName());
            preparedStatement.setLong(4, account.getGrade_end());
            preparedStatement.setInt(5, account.getCoins());
            preparedStatement.setInt(6, account.getLevel());
            preparedStatement.setInt(7, account.getXp());
            preparedStatement.setString(8, account.getGuild());
            preparedStatement.setBoolean(9, account.isBanned());
            preparedStatement.setString(10, account.getBan_id());
            preparedStatement.setBoolean(11, account.isMuted());
            preparedStatement.setString(12, account.getMute_id());
            preparedStatement.setBoolean(13, account.isModeration());
            preparedStatement.setBoolean(14, account.isNicked());
            preparedStatement.setBoolean(15, account.isConnected());
            preparedStatement.setString(16, account.getUuid().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();

            if(account.getCases() != null){
                account.getCases().forEach(this::updateMySQLAccountCase);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMySQLAccountCase(AccountCase accountCase){
        final Connection connection;
        try {
            connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO "+ ACCOUNT_CASE_TABLE + " (id, uuid, type, reason, end_time, uuid_moderator, cancel_moderator, cancel_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE reason = VALUES(reason), end_time = VALUES(end_time), uuid_moderator = VALUES(uuid_moderator), cancel_moderator = VALUES(cancel_moderator), cancel_reason = VALUES(cancel_reason)");
            preparedStatement.setString(1, accountCase.getId());
            preparedStatement.setString(2, accountCase.getUuid().toString());
            preparedStatement.setString(3, accountCase.getType());
            preparedStatement.setString(4, accountCase.getReason());
            preparedStatement.setLong(5, accountCase.getEnd_time());
            preparedStatement.setString(6, accountCase.getUuid_moderator().toString());
            preparedStatement.setString(7, accountCase.getCancel_moderator() == null ? null : accountCase.getCancel_moderator().toString());
            preparedStatement.setString(8, accountCase.getCancel_reason());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account getAccountFromRedis(){
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        final String key = REDIS_KEY + this.uuid.toString();
        final RBucket<Account> accountRBucket = redissonClient.getBucket(key);

        return accountRBucket.get();
    }

    public Account getAccountFromMySQL(){
        Account account = null;
        try {
            final Connection connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+ ACCOUNT_TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            if(resultSet.next()){
                final String username = resultSet.getString("username");
                final String nickname = resultSet.getString("nickname");
                final RankUnit grade = RankUnit.getByName(resultSet.getString("grade"));
                final long grade_end = resultSet.getLong("grade_end");
                final int coins = resultSet.getInt("coins");
                final int level = resultSet.getInt("level");
                final int xp = resultSet.getInt("xp");
                final String guild = resultSet.getString("guild");
                final boolean isBanned = resultSet.getBoolean("isBanned");
                final String ban_id = resultSet.getString("ban_id");
                final boolean isMuted = resultSet.getBoolean("isMuted");
                final String mute_id = resultSet.getString("mute_id");
                final boolean isModeration = resultSet.getBoolean("isModeration");
                final boolean isNicked = resultSet.getBoolean("isNicked");
                final boolean isConnected = resultSet.getBoolean("isConnected");

                List<AccountCase> cases = getCases();

                account = new Account(uuid, username, nickname, grade, grade_end, coins, level, xp, guild, cases, isBanned, ban_id, isMuted, mute_id, false, false, isModeration, isNicked, isConnected, null);
                preparedStatement.close();
                connection.close();
                return account;
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public List<AccountCase> getCases(){
        List<AccountCase> cases = new ArrayList<>();
        try {
            final Connection connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+ ACCOUNT_CASE_TABLE + " WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeQuery();

            final ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()){
                AccountCase accountCase;
                final String id = resultSet.getString("id");
                final UUID target_uuid = UUID.fromString(resultSet.getString("uuid"));
                final String type = resultSet.getString("type");
                final String reason = resultSet.getString("reason");
                final long end_time = resultSet.getLong("end_time");
                final UUID uuid_moderator = UUID.fromString(resultSet.getString("uuid_moderator"));
                if(resultSet.getString("cancel_moderator") != null && resultSet.getString("cancel_reason") != null){
                    final UUID cancel_moderator = UUID.fromString(resultSet.getString("cancel_moderator"));
                    final String cancel_reason = resultSet.getString("cancel_reason");
                    accountCase = new AccountCase(id, target_uuid, type, reason, end_time, uuid_moderator, cancel_moderator, cancel_reason);
                }else{
                    accountCase = new AccountCase(id, target_uuid, type, reason, end_time, uuid_moderator);
                }
                cases.add(accountCase);
            }
            preparedStatement.close();
            connection.close();
            return cases;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }

    private Account createNewAccount(UUID uuid) {
        try {

            uuid = UUID.fromString(MojangAPI.getMojangAPI().getPlayerProfile(uuid.toString()).getUUID());
            String playername = MojangAPI.getMojangAPI().getPlayerProfile(uuid.toString()).getUsername();

            if(MojangAPI.getMojangAPI().getPlayerProfile(uuid.toString()).getUUID() == null){
                System.err.println("[MojangAPI] Le joueur '" + uuid + "' n'existe pas.");
                return null;
            }

            final Account account = DEFAULT_ACCOUNT.clone();
            final Connection connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + ACCOUNT_TABLE + " (uuid, username, nickname, grade, grade_end, coins, level, xp, guild, isBanned, ban_id, isMuted, mute_id, isModeration, isNicked, isConnected) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, playername);
            preparedStatement.setString(3, account.getNickname());
            preparedStatement.setString(4, account.getGrade().getName());
            preparedStatement.setLong(5, account.getGrade_end());
            preparedStatement.setInt(6, account.getCoins());
            preparedStatement.setInt(7, account.getLevel());
            preparedStatement.setInt(8, account.getXp());
            preparedStatement.setString(9, account.getGuild());
            preparedStatement.setBoolean(10, account.isBanned());
            preparedStatement.setString(11, account.getBan_id());
            preparedStatement.setBoolean(12, account.isMuted());
            preparedStatement.setString(13, account.getMute_id());
            preparedStatement.setBoolean(14, account.isModeration());
            preparedStatement.setBoolean(15, account.isNicked());
            preparedStatement.setBoolean(16, account.isConnected());

            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            return account;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void createAccountsTables(){
        try {
            final Connection connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + ACCOUNT_TABLE + "` "
                    + "(`uuid` VARCHAR(500) NOT NULL,"
                    + "`username` VARCHAR(255) NOT NULL,"
                    + "`nickname` VARCHAR(255) DEFAULT NULL,"
                    + "`grade` VARCHAR(255) NOT NULL DEFAULT 'Joueur',"
                    + "`grade_end` BIGINT NOT NULL DEFAULT '-1',"
                    + "`coins` INT NOT NULL DEFAULT '0',"
                    + "`level` INT NOT NULL DEFAULT '1',"
                    + "`xp` INT NOT NULL DEFAULT '0',"
                    + "`guild` VARCHAR(255) DEFAULT NULL,"
                    + "`isBanned` TINYINT NOT NULL DEFAULT '0',"
                    + "`ban_id` VARCHAR(500) DEFAULT NULL,"
                    + "`isMuted` TINYINT NOT NULL DEFAULT '0',"
                    + "`mute_id` VARCHAR(500) DEFAULT NULL,"
                    + "`isModeration` TINYINT NOT NULL DEFAULT '0',"
                    + "`isNicked` TINYINT NOT NULL DEFAULT '0',"
                    + "`isConnected` TINYINT NOT NULL DEFAULT 0,"
                    + "PRIMARY KEY (`uuid`), UNIQUE KEY `uuid_UNIQUE` (`uuid`))");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void createAccountsCasesTables(){
        try {
            final Connection connection = MySQLManager.MGC.getMySQLAccess().getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + ACCOUNT_CASE_TABLE + "` "
                    + "(`id` VARCHAR(500) NOT NULL,"
                    + "`uuid` VARCHAR(500) NOT NULL,"
                    + "`type` VARCHAR(255) DEFAULT NULL,"
                    + "`reason` VARCHAR(255) NOT NULL DEFAULT 'Joueur',"
                    + "`end_time` VARCHAR(255) NOT NULL DEFAULT '-1',"
                    + "`uuid_moderator` VARCHAR(500) NOT NULL DEFAULT 'Kronos',"
                    + "`unban_moderator` VARCHAR(500) DEFAULT NULL,"
                    + "`unban_reason` VARCHAR(255) DEFAULT NULL,"
                    + "PRIMARY KEY (`id`), UNIQUE KEY `id_UNIQUE` (`id`))");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
