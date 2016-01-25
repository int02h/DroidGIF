package com.dpforge.droidgif.decoder2;

public enum DisposalMethod {
	NOT_SPECIFIED,
	NO_DISPOSE,
	RESTORE_BACKGROUND,
	RESTORE_PREVIOUS;

	static DisposalMethod fromInt(int value) {
		switch (value) {
			case 1:
				return NO_DISPOSE;
			case 2:
				return RESTORE_BACKGROUND;
			case 3:
				return RESTORE_PREVIOUS;
			default:
				return NOT_SPECIFIED;
		}
	}
}
