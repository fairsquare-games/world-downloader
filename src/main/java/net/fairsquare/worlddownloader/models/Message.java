package net.fairsquare.worlddownloader.models;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public enum Message {

    PREFIX("&8[]&r "),
    NOT_A_PLAYER("&cIf you are not on the server, you must provid the name of the world you want to download."),
    COMMAND_USAGE("&eUsage: &6/downloadworld &8[&6name&8]"),
    WORLD_NOT_FOUND("&cCouldn't retrieve the world file."),
    WORLD_NOT_FOUND_NAME("&cCouldn't find a world named &4%1$s&c."),
    DATA_FOLDER_INACCESSIBLE("&cCouldn't access plugin's data folder."),
    CREATING_ZIP("&eZipping world &6%1$s&e..."),
    CREATED_ZIP("&eZipped world &6%1$s&e!"),
    ERROR_CREATING_ZIP("&cAn exception occurred whilst trying to create a zip archive. Please contact an administrator."),
    ERROR_UPLOADING_ZIP("&cAn exception occurred whilst trying to upload the zip archive. Please contact an administrator."),
    UPLOADING_ZIP("&eUploading zip archive..."),
    UPLOADED_ZIP("&eUploaded zip archive!"),
    DOWNLOAD_URL("&eYou can download the world from the following URL: &6%1$s&e.");

    private final String text;

    Message(String text) {
        this.text = text;
    }

    public String getText(Object... args) {
        return ChatColor.translateAlternateColorCodes('&',
                String.format(this.text, args));
    }

    public BaseComponent[] getTextComponent(Object... args) {
        return TextComponent.fromLegacyText(getText(args));
    }

    public void send(CommandSender target, Object... args) {
        target.spigot().sendMessage(TextComponent
                .fromLegacyText(Message.PREFIX.getText() + getText(args)));
    }

}
