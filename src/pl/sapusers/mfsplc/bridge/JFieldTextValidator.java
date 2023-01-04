package pl.sapusers.mfsplc.bridge;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Helper class to validate JFieldText content
 * Supports max string length check if maxValue == 0 or max int value check if maxValue != 0 
 */
@SuppressWarnings("serial")
public class JFieldTextValidator extends PlainDocument {
	private int maxLength;
	private int maxValue;

	/**
	 * @param maxLength Max string length
	 * @param maxValue Max integer value
	 */
	public JFieldTextValidator(int maxLength, int maxValue) {
		super();
		this.maxLength = maxLength;
		this.maxValue = maxValue;
	}

	/**
	 * Validates max string length if maxValue has not been provided
	 * Interprets string as integer and validates its value against maxValue
	 */
	@Override
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
