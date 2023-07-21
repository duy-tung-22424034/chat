
package client;

import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author tungpd
 */
public class MessageStyle {

    /*
     * Message Content Style
     */
    public static AttributeSet styleMessageContent(Color color, String fontFamily, int size) {
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground,
                color);

        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, fontFamily); // FontFamily
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment,
                StyleConstants.ALIGN_JUSTIFIED);
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontSize, size);
        return attributeSet;
    }
}
