package me.imnowapro.proreplay.replay.recording.converter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.imnowapro.proreplay.replay.recording.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PacketConverter_1_8_R3 implements PacketConverter {

  private static final Map<PacketType, Integer> PACKET_IDS = new HashMap<>();

  static {
    PACKET_IDS.put(PacketType.Login.Server.SUCCESS, 0x02);
    PACKET_IDS.put(PacketType.Play.Server.LOGIN, 0x01);
    PACKET_IDS.put(PacketType.Play.Server.CHAT, 0x02);
    PACKET_IDS.put(PacketType.Play.Server.UPDATE_TIME, 0x03);
    PACKET_IDS.put(PacketType.Play.Server.ENTITY_EQUIPMENT, 0x04);
    PACKET_IDS.put(PacketType.Play.Server.SPAWN_POSITION, 0x05);
    PACKET_IDS.put(PacketType.Play.Server.RESPAWN, 0x07);
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
    PACKET_IDS.put(PacketType.Play.Server.MAP_CHUNK, 0x21);
    PACKET_IDS.put(PacketType.Play.Server.MAP_CHUNK_BULK, 0x26);
    PACKET_IDS.put(PacketType.Play.Server.PLAYER_INFO, 0x38);
    PACKET_IDS.put(PacketType.Play.Server.WORLD_BORDER, 0x44);
  }

  @Override
  public int getPacketID(PacketType type) {
    return PACKET_IDS.getOrDefault(type, type.getCurrentId());
  }

  @Override
  public PacketContainer createLoginSuccessPacket(Player player) {
    PacketContainer packet = new PacketContainer(PacketType.Login.Server.SUCCESS);
    packet.getGameProfiles().write(0,
        new WrappedGameProfile(UUID.nameUUIDFromBytes(new byte[0]), "Player"));
    return packet;
  }

  @Override
  public PacketContainer createCameraPacket(Player player) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.CAMERA);
    packet.getIntegers().write(0, player.getEntityId());
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
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getUUIDs().write(0, player.getUniqueId());
    packet.getIntegers().write(1, (int) (player.getLocation().getX() * 32))
        .write(2, (int) (player.getLocation().getY() * 32))
        .write(3, (int) (player.getLocation().getZ() * 32));
    packet.getBytes().write(0, PacketUtil.toAngle(player.getLocation().getYaw()))
        .write(1, PacketUtil.toAngle(player.getLocation().getPitch()));
    packet.getIntegers().write(4,
        (player.getItemInHand() != null ? player.getItemInHand().getTypeId() : 0));
    packet.getDataWatcherModifier().write(0, WrappedDataWatcher.getEntityWatcher(player));
    return packet;
  }

  @Override
  public PacketContainer convertPositionPacket(Player player, PacketContainer oldPacket) {
    Vector move = new Vector(oldPacket.getDoubles().read(0) - player.getLocation().getX(),
        oldPacket.getDoubles().read(1) - player.getLocation().getY(),
        oldPacket.getDoubles().read(2) - player.getLocation().getZ());
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, PacketUtil.toFixedPointNumber(move.getX()))
        .write(1, PacketUtil.toFixedPointNumber(move.getY()))
        .write(2, PacketUtil.toFixedPointNumber(move.getZ()));
    packet.getBooleans().write(0, oldPacket.getBooleans().read(0));
    return packet;
  }

  @Override
  public PacketContainer convertPositionLookPacket(Player player, PacketContainer oldPacket) {
    Vector move = new Vector(oldPacket.getDoubles().read(0) - player.getLocation().getX(),
        oldPacket.getDoubles().read(1) - player.getLocation().getY(),
        oldPacket.getDoubles().read(2) - player.getLocation().getZ());
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, PacketUtil.toFixedPointNumber(move.getX()))
        .write(1, PacketUtil.toFixedPointNumber(move.getY()))
        .write(2, PacketUtil.toFixedPointNumber(move.getZ()));
    packet.getBytes().write(3, PacketUtil.toAngle(oldPacket.getFloat().read(0)));
    packet.getBytes().write(4, PacketUtil.toAngle(oldPacket.getFloat().read(1)));
    packet.getBooleans().write(0, oldPacket.getBooleans().read(0));
    return packet;
  }

  @Override
  public PacketContainer convertLookPacket(Player player, PacketContainer oldPacket) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getBytes().write(0, PacketUtil.toAngle(oldPacket.getFloat().read(0)));
    packet.getBytes().write(1, PacketUtil.toAngle(oldPacket.getFloat().read(1)));
    packet.getBooleans().write(0, oldPacket.getBooleans().read(0));
    return packet;
  }
}
