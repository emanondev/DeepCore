package emanondev.core.actions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SoundAction extends Action {

    public SoundAction() {
        super("sound");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
        String[] args = text.split(" ");
        if (args.length > 4)
            throw new IllegalStateException();
        Sound.valueOf(args[0].toUpperCase());
        if (args.length >= 2) {
            if (Float.parseFloat(args[1]) <= 0)
                throw new IllegalStateException();
        }
        if (args.length >= 3) {
            if (Float.parseFloat(args[2]) <= 0)
                throw new IllegalStateException();
        }
        if (args.length >= 4) {
            switch (args[3].toLowerCase(Locale.ENGLISH)) {
                case "true":
                case "false":
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        boolean self = false;
        float volume = 1;
        float pitch = 1;
        Sound sound = Sound.valueOf(args[0].toUpperCase());
        if (args.length >= 2)
            volume = Float.parseFloat(args[1]);
        if (args.length >= 3)
            pitch = Float.parseFloat(args[2]);
        if (args.length >= 4)
            self = Boolean.parseBoolean(args[3]);
        if (self)
            player.playSound(player.getLocation(), sound, volume, pitch);
        else
            player.getLocation().getWorld().playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<sound> [volume] [pitch] [self]");
        list.add("&e<sound> &bthe sound to play");
        list.add("&e[volume] &bthe volume of the sound, default 1");
        list.add("&e[pitch] &bthe pitch of the sound, default 1");
        list.add("&e[self] &bonly player sould heard?, by default false");
        return list;
    }
}
