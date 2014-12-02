package net.frozenorb.foxtrot.imagemessage;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

// All credits for this class goes to http://forums.bukkit.org/threads/lib-imagemessage-v2-1-send-images-to-players-via-the-chat.204902/.
public class ImageMessage {

    private final static char TRANSPARENT_CHAR = ' ';
    private final static Color[] COLORS = {
            new Color(0, 0, 0),
            new Color(0, 0, 170),
            new Color(0, 170, 0),
            new Color(0, 170, 170),
            new Color(170, 0, 0),
            new Color(170, 0, 170),
            new Color(255, 170, 0),
            new Color(170, 170, 170),
            new Color(85, 85, 85),
            new Color(85, 85, 255),
            new Color(85, 255, 85),
            new Color(85, 255, 255),
            new Color(255, 85, 85),
            new Color(255, 85, 255),
            new Color(255, 255, 85),
            new Color(255, 255, 255),
    };

    @Getter private String[] lines;

    public ImageMessage(String image) {
        try {
            ChatColor[][] chatColors = toChatColorArray(ImageIO.read(new File(new File("ascii-art"), image + ".png")), 8);
            lines = toImgMessage(chatColors, '\u2588');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImageMessage appendText(String... text) {
        for (int y = 0; y < lines.length; y++) {
            if (text.length > y) {
                lines[y] += " " + ChatColor.translateAlternateColorCodes('&', text[y]);
            }
        }

        return (this);
    }

    private ChatColor[][] toChatColorArray(BufferedImage image, int height) {
        double ratio = (double) image.getHeight() / image.getWidth();
        BufferedImage resized = resizeImage(image, (int) (height / ratio), height);
        ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];

        for (int x = 0; x < resized.getWidth(); x++) {
            for (int y = 0; y < resized.getHeight(); y++) {
                int rgb = resized.getRGB(x, y);
                ChatColor closest = getClosestChatColor(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }

        return (chatImg);
    }

    private String[] toImgMessage(ChatColor[][] colors, char imgchar) {
        String[] lines = new String[colors[0].length];

        for (int y = 0; y < colors[0].length; y++) {
            String line = "";

            for (int x = 0; x < colors.length; x++) {
                ChatColor color = colors[x][y];
                line += (color != null) ? colors[x][y].toString() + imgchar : TRANSPARENT_CHAR;
            }

            lines[y] = line + ChatColor.RESET;
        }

        return (lines);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        AffineTransform af = new AffineTransform();
        af.scale(
                width / (double) originalImage.getWidth(),
                height / (double) originalImage.getHeight());

        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return (operation.filter(originalImage, null));
    }

    private double getDistance(Color c1, Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        double weightR = 2 + rmean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256.0;
        return (weightR * r * r + weightG * g * g + weightB * b * b);
    }

    private boolean areIdentical(Color c1, Color c2) {
        return (Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
                Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
                Math.abs(c1.getBlue() - c2.getBlue()) <= 5);
    }

    private ChatColor getClosestChatColor(Color color) {
        if (color.getAlpha() < 128) {
            return (null);
        }

        int index = 0;
        double best = -1;

        for (int i = 0; i < COLORS.length; i++) {
            if (areIdentical(COLORS[i], color)) {
                return (ChatColor.values()[i]);
            }
        }

        for (int i = 0; i < COLORS.length; i++) {
            double distance = getDistance(color, COLORS[i]);

            if (distance < best || best == -1) {
                best = distance;
                index = i;
            }
        }

        return (ChatColor.values()[index]);
    }

    public void send(CommandSender sender) {
        for (String line : lines) {
            sender.sendMessage(line);
        }
    }

    public void broadcast() {
        for (String line : lines) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(line);
        }
    }

    public void sendOPs() {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                send(player);
            }
        }
    }

}