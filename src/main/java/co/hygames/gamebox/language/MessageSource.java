package co.hygames.gamebox.language;

import java.util.List;

/**
 * Interface for supplying messages via keys.
 *
 * @author Niklas Eicker
 */
public interface MessageSource {
    String getMessage(String key);

    List<String> getMessageList(String key);
}
