package me.imnowapro.proreplay.replay.recording.converter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import java.util.Collections;
import me.imnowapro.proreplay.replay.recording.PacketUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PacketConverter_1_15_R1 implements PacketConverter {

  @Override
  public int getPacketID(PacketType type) {
    return 0;
  }

  @Override
  public PacketContainer createLoginSuccessPacket(Player player) {
    // TODO
    return null;
  }

  @Override
  public PacketContainer createCameraPacket(Player player) {
    // TODO
    return null;
  }

  @Override
  public PacketContainer createLoginPacket(Player player) {
    // TODO
    return null;
  }

  @Override
  public PacketContainer createPlayerListItemPacket(Player player) {
    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player),
        1,
        EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
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
    packet.getDoubles().write(0, player.getLocation().getX() * 32)
        .write(1, player.getLocation().getY() * 32)
        .write(2, player.getLocation().getZ() * 32);
    packet.getBytes().write(0, PacketUtil.toAngle(player.getLocation().getYaw()))
        .write(1, PacketUtil.toAngle(player.getLocation().getPitch()));
    return packet;
  }

  @Override
  public PacketContainer convertPositionPacket(Player player, PacketContainer oldPacket) {
    Vector move = new Vector(oldPacket.getDoubles().read(0) - player.getLocation().getX(),
        oldPacket.getDoubles().read(1) - player.getLocation().getY(),
        oldPacket.getDoubles().read(2) - player.getLocation().getZ());
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
    packet.getIntegers().write(0, player.getEntityId());
    packet.getShorts().write(0, (short) PacketUtil.toFixedPointNumber(move.getX()))
        .write(1, (short) PacketUtil.toFixedPointNumber(move.getY()))
        .write(2, (short) PacketUtil.toFixedPointNumber(move.getZ()));
    packet.getBooleans().write(0, oldPacket.getBooleans().read(0));
    return packet;
  }

  @Override
  public PacketContainer convertPositionLookPacket(Player player, PacketContainer oldPacket) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
        convertPositionPacket(player, oldPacket).getHandle());
    packet.getBytes().write(0, PacketUtil.toAngle(oldPacket.getFloat().read(0)));
    packet.getBytes().write(1, PacketUtil.toAngle(oldPacket.getFloat().read(1)));
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
