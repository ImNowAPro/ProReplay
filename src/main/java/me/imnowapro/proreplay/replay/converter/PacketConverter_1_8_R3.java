package me.imnowapro.proreplay.replay.converter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import java.util.UUID;
import me.imnowapro.proreplay.replay.recording.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PacketConverter_1_8_R3 implements PacketConverter {

  private static final BiMap<PacketType, Integer> PACKET_IDS = HashBiMap.create();

  static {
    // Only contains necessary packets.
    PACKET_IDS.put(PacketType.Play.Server.LOGIN, 0x01);
    PACKET_IDS.put(PacketType.Play.Server.CHAT, 0x02);
    PACKET_IDS.put(PacketType.Play.Server.UPDATE_TIME, 0x03);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_EQUIPMENT, 0x04);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_POSITION, 0x05);
    PACKET_IDS.put(PacketType.Play.Server.RESPAWN, 0x07);
    PACKET_IDS.put(PacketType.Play.Server.POSITION, 0x8);
    PACKET_IDS.put(PacketType.Play.Server.BED, 0x0A);
    PACKET_IDS.put(PacketType.Play.Server.ANIMATION, 0x0B);
    PACKET_IDS.put(PacketType.Play.Server.NAMED_ENTITY_SPAWN, 0x0C);
    PACKET_IDS.put(PacketType.Play.Server.COLLECT, 0x0D);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_ENTITY, 0x0E);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_ENTITY_LIVING, 0x0F);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_ENTITY_PAINTING, 0x10);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB, 0x11);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_VELOCITY, 0x12);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_DESTROY, 0x13);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY, 0x14);
    PACKET_IDS.put(PacketType.Play.Server.REL_ENTITY_MOVE, 0x15);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_LOOK, 0x16);
    PACKET_IDS.put(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, 0x17);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_TELEPORT, 0x18);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_HEAD_ROTATION, 0x19);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_STATUS, 0x1A);
    PACKET_IDS.put(PacketType.Play.Server.ATTACH_ENTITY, 0x1B);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_METADATA, 0x1C);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_EFFECT, 0x1D);
    PACKET_IDS.put(PacketType.Play.Server.REMOVE_ENTITY_EFFECT, 0x1E);
    PACKET_IDS.put(PacketType.Play.Server.EXPERIENCE, 0x1F);
    PACKET_IDS.put(PacketType.Play.Server.UPDATE_ATTRIBUTES, 0x20);
    PACKET_IDS.put(PacketType.Play.Server.MAP_CHUNK, 0x21);
    PACKET_IDS.put(PacketType.Play.Server.MULTI_BLOCK_CHANGE, 0x22);
    PACKET_IDS.put(PacketType.Play.Server.BLOCK_CHANGE, 0x23);
    PACKET_IDS.put(PacketType.Play.Server.BLOCK_ACTION, 0x24);
    PACKET_IDS.put(PacketType.Play.Server.BLOCK_BREAK_ANIMATION, 0x25);
    PACKET_IDS.put(PacketType.Play.Server.MAP_CHUNK_BULK, 0x26);
    PACKET_IDS.put(PacketType.Play.Server.EXPLOSION, 0x27);
    PACKET_IDS.put(PacketType.Play.Server.WORLD_EVENT, 0x28);
    PACKET_IDS.put(PacketType.Play.Server.NAMED_SOUND_EFFECT, 0x29);
    PACKET_IDS.put(PacketType.Play.Server.WORLD_PARTICLES, 0x2A);
    PACKET_IDS.put(PacketType.Play.Server.GAME_STATE_CHANGE, 0x2B);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_ENTITY_WEATHER, 0x2C);
    PACKET_IDS.put(PacketType.Play.Server.UPDATE_SIGN, 0x33);
    PACKET_IDS.put(PacketType.Play.Server.PLAYER_INFO, 0x38);
    PACKET_IDS.put(PacketType.Play.Server.SCOREBOARD_OBJECTIVE, 0x3B);
    PACKET_IDS.put(PacketType.Play.Server.SCOREBOARD_SCORE, 0x3C);
    PACKET_IDS.put(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE, 0x3D);
    PACKET_IDS.put(PacketType.Play.Server.SCOREBOARD_TEAM, 0x3E);
    PACKET_IDS.put(PacketType.Play.Server.VIEW_CENTRE, 0x41);
    PACKET_IDS.put(PacketType.Play.Server.CAMERA, 0x43);
    PACKET_IDS.put(PacketType.Play.Server.WORLD_BORDER, 0x44);
    PACKET_IDS.put(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER, 0x47);
    PACKET_IDS.put(PacketType.Play.Server.UPDATE_ENTITY_NBT, 0x49);
  }

  @Override
  public int getPacketID(PacketType type) {
    if (type.getProtocol().equals(PacketType.Protocol.PLAY)) {
      return PACKET_IDS.getOrDefault(type, type.getCurrentId());
    }
    return type.getCurrentId();
  }

  @Override
  public PacketType getPacketType(int id) {
    return PACKET_IDS.inverse().getOrDefault(id,
        PacketType.findCurrent(PacketType.Protocol.PLAY, PacketType.Sender.SERVER, id));
  }

  @Override
  public PacketContainer createLoginSuccessPacket(Player player) {
    PacketContainer packet = new PacketContainer(PacketType.Login.Server.SUCCESS);
    packet.getGameProfiles().write(0,
        new WrappedGameProfile(UUID.nameUUIDFromBytes(new byte[0]), "Player"));
    return packet;
  }

  @Override
  public PacketContainer createLoginPacket(Player player) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.LOGIN);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getGameModes().write(0, EnumWrappers.NativeGameMode.NOT_SET);
    packet.getIntegers().write(1, player.getWorld().getDifficulty().getValue());
    packet.getDifficulties().write(0,
        EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().name()));
    packet.getIntegers().write(2, Bukkit.getMaxPlayers());
    packet.getWorldTypeModifier().write(0, player.getWorld().getWorldType());
    packet.getBooleans().write(0, false);
    return packet;
  }

  @Override
  public PacketContainer createPlayerListItemPacket(Player player) {
    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player),
        1,
        EnumWrappers.NativeGameMode.NOT_SET,
        WrappedChatComponent.fromText(player.getDisplayName()));
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
    packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
    return packet;
  }

  @Override
  public PacketContainer createPlayerSpawnPacket(Player player) {
    return createPlayerSpawnPacket(player, player.getLocation());
  }

  @Override
  public PacketContainer createPlayerSpawnPacket(Player player, Location location) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getUUIDs().write(0, player.getUniqueId());
    packet.getIntegers().write(1, PacketUtil.toFixedPointNumber(location.getX()))
        .write(2, PacketUtil.toFixedPointNumber(location.getY()))
        .write(3, PacketUtil.toFixedPointNumber(location.getZ()));
    packet.getBytes().write(0, PacketUtil.toAngle(player.getLocation().getYaw()))
        .write(1, PacketUtil.toAngle(player.getLocation().getPitch()));
    packet.getIntegers().write(4,
        (player.getItemInHand() != null ? player.getItemInHand().getTypeId() : 0));
    packet.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(player));
    return packet;
  }

  @Override
  public PacketContainer createPositionPacket(Location position) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.POSITION);
    packet.getDoubles().write(0, position.getX())
        .write(1, position.getY())
        .write(2, position.getZ());
    packet.getFloat().write(0, position.getYaw())
        .write(1, position.getPitch());
    WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
    dataWatcher.setObject(0, (byte) 0);
    packet.getWatchableCollectionModifier().writeDefaults();
    return packet;
  }

  @Override
  public PacketContainer createPositionPacket(Player player, Vector move) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, (byte) PacketUtil.toFixedPointNumber(move.getX()))
        .write(1, (byte) PacketUtil.toFixedPointNumber(move.getY()))
        .write(2, (byte) PacketUtil.toFixedPointNumber(move.getZ()));
    packet.getBooleans().write(0, player.isOnGround());
    return packet;
  }

  @Override
  public PacketContainer createPositionLookPacket(Player player, Vector move, float yaw,
                                                  float pitch) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, (byte) PacketUtil.toFixedPointNumber(move.getX()))
        .write(1, (byte) PacketUtil.toFixedPointNumber(move.getY()))
        .write(2, (byte) PacketUtil.toFixedPointNumber(move.getZ()));
    packet.getBytes().write(3, PacketUtil.toAngle(yaw));
    packet.getBytes().write(4, PacketUtil.toAngle(pitch));
    packet.getBooleans().write(0, player.isOnGround());
    return packet;
  }

  @Override
  public PacketContainer createLookPacket(Player player, float yaw, float pitch) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, PacketUtil.toAngle(yaw));
    packet.getBytes().write(1, PacketUtil.toAngle(pitch));
    packet.getBooleans().write(0, player.isOnGround());
    return packet;
  }

  @Override
  public PacketContainer createTeleportPacket(Player player, Location location) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
    packet.getIntegers().write(0, player.getEntityId())
        .write(1, PacketUtil.toFixedPointNumber(player.getLocation().getX()))
        .write(2, PacketUtil.toFixedPointNumber(player.getLocation().getY()))
        .write(3, PacketUtil.toFixedPointNumber(player.getLocation().getZ()));
    packet.getBytes().write(0, PacketUtil.toAngle(player.getLocation().getYaw()))
        .write(1, PacketUtil.toAngle(player.getLocation().getPitch()));
    packet.getBooleans().write(0, player.isOnGround());
    return packet;
  }

  @Override
  public PacketContainer createHeadRotationPacket(Player player, float yaw) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, PacketUtil.toAngle(yaw));
    return packet;
  }

  @Override
  public PacketContainer convertAnimationPacket(Player player, PacketContainer oldPacket) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ANIMATION);
    packet.getIntegers().write(0, player.getEntityId())
        .write(1, 0);
    return packet;
  }
}
