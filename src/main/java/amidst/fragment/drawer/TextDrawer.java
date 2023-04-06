package amidst.fragment.drawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class TextDrawer {

    private static final Font DRAW_FONT = new Font("arial", Font.BOLD, 16);

    private final StringBuffer textBuffer = new StringBuffer(128);
    private final char[] textCache = new char[128];

    @CalledOnlyBy(AmidstThread.EDT)
    public void updateText(Fragment fragment) {
        textBuffer.setLength(0);
        textBuffer.append(fragment.getCorner().getX());
        textBuffer.append(", ");
        textBuffer.append(fragment.getCorner().getY());
        textBuffer.getChars(0, textBuffer.length(), textCache, 0);
    }

    @CalledOnlyBy(AmidstThread.EDT)
    public void drawText(Graphics2D g2d) {
        g2d.drawChars(textCache, 0, textBuffer.length(), 12, 17);
        g2d.drawChars(textCache, 0, textBuffer.length(), 8, 17);
        g2d.drawChars(textCache, 0, textBuffer.length(), 10, 19);
        g2d.drawChars(textCache, 0, textBuffer.length(), 10, 15);
    }

    // This makes the text outline a bit thicker, but seems unneeded.
    @SuppressWarnings("unused")
    private void drawThickTextOutline(Graphics2D g2d) {
        g2d.drawChars(textCache, 0, textBuffer.length(), 12, 15);
        g2d.drawChars(textCache, 0, textBuffer.length(), 12, 19);
        g2d.drawChars(textCache, 0, textBuffer.length(), 8, 15);
        g2d.drawChars(textCache, 0, textBuffer.length(), 8, 19);
    }

    @CalledOnlyBy(AmidstThread.EDT)
    public void drawTextOutline(Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.drawChars(textCache, 0, textBuffer.length(), 10, 17);
    }

    @CalledOnlyBy(AmidstThread.EDT)
    public void initGraphics(Graphics2D g2d) {
        g2d.setFont(DRAW_FONT);
        g2d.setColor(Color.black);
    }

}
