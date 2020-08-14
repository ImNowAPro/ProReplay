package me.imnowapro.proreplay.replay.recording.converter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.logging.Level;
import me.imnowapro.proreplay.ProReplay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface PacketConverter {

  int getPacketID(PacketType type);

  PacketType getPacketType(int id);

  PacketContainer createLoginSuccessPacket(Player player);

  PacketContainer createLoginPacket(Player player);

  PacketContainer createPlayerListItemPacket(Player player);

  PacketContainer createPlayerSpawnPacket(Player player);

  PacketContainer createPositionPacket(Location position);

  PacketContainer createPositionPacket(Player player, Vector move);

  PacketContainer createPositionLookPacket(Player player, Vector move, float yaw, float pitch);

  PacketContainer createLookPacket(Player player, float yaw, float pitch);

  PacketContainer createHeadRotationPacket(Player player, float yaw);

  static Optional<PacketConverter> getConverter() {
    try {
      String version = Bukkit.getServer().getClass().getPackage().getName()
          .replace(".", ",").split(",")[3].substring(1);
      return Optional.of((PacketConverter) Class
          .forName(PacketConverter.class.getCanonicalName() + "_" + version)
          .getConstructors()[0].newInstance());
    } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException | InvocationTargetException e) {
      ProReplay.getInstance().getLogger().log(Level.WARNING, "Failed to get PacketConverter.", e);
    }
    return Optional.empty();
  }
}
