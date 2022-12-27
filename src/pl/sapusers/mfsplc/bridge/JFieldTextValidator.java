package pl.sapusers.mfsplc.bridge;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class JFieldTextValidator extends PlainDocument {
	private int maxLength;
	private int maxValue;

	public JFieldTextValidator(int maxLength, int maxValue) {
		super();
		this.maxLength = maxLength;
		this.maxValue = maxValue;
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

		if (maxValue == 0) {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= maxLength)
				super.insertString(offs, str, a);
			
		} else {
			int value;
			try {
				value = Integer.parseUnsignedInt(getText(0, offs) + str + getText(offs, getLength() - offs));
			} catch (NumberFormatException e) {
				return;
			}
			if (value <= maxValue)
				super.insertString(offs, str, a);
		}
	}

}
