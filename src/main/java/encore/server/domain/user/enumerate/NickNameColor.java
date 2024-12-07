package encore.server.domain.user.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NickNameColor {
    RED("Red"), BLUE("Blue"), GREEN("Green"), YELLOW("Yellow"),
    PURPLE("Purple"), ORANGE("Orange"), PINK("Pink"), BLACK("Black"),
    WHITE("White"), GRAY("Gray"), CYAN("Cyan"), MAGENTA("Magenta"),
    BROWN("Brown"), VIOLET("Violet"), INDIGO("Indigo"), TEAL("Teal"),
    NAVY("Navy"), MAROON("Maroon"), OLIVE("Olive"), TURQUOISE("Turquoise"),
    BEIGE("Beige"), LIME("Lime"), CORAL("Coral"), GOLD("Gold"),
    SILVER("Silver"), AQUA("Aqua"), SALMON("Salmon"), PLUM("Plum");

    private final String colorName;

}
