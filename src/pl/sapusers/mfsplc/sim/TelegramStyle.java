package pl.sapusers.mfsplc.sim;

import java.awt.Color;

public class TelegramStyle {
	private String name;
	private Color color;
	private boolean italic = false;
	private boolean bold = false;
	private boolean underline = false;
	private boolean strikeThrough = false;

	public TelegramStyle(String propertyKey, String propertyValue) {
		name = propertyKey.substring(6);

		String[] styleParts = propertyValue.split(",");
		color = new Color(Integer.parseInt(styleParts[0]), Integer.parseInt(styleParts[1]),
				Integer.parseInt(styleParts[2]));

		if (styleParts.length == 4) {
			if (styleParts[3].contains("I"))
				italic = true;
			if (styleParts[3].contains("B"))
				bold = true;
			if (styleParts[3].contains("U"))
				underline = true;
			if (styleParts[3].contains("S"))
				strikeThrough = true;
		}

	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public boolean isStrikeThrough() {
		return strikeThrough;
	}

	public boolean isUnderline() {
		return underline;
	}

}
