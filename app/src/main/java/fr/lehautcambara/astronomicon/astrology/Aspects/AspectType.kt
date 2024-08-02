package fr.lehautcambara.astronomicon.astrology.Aspects

import androidx.compose.ui.graphics.Color

enum class AspectType {
    Conjunction {
        override val influentiality get() = 1
        override val glyph: Int? get() = null
        override val angle get() = 0.0
        override val color: Color
            get() = Color.White
    },

    Opposition {
        override val influentiality get() = 1
        override val glyph: Int?
            get() = null
        override val angle get() = 180.0
        override val color: Color
            get() = Color.Black
    },

    Trine {
        override val influentiality get() = 2
        override val glyph: Int?
            get() = null
        override val angle get() = 120.0
        override val color: Color
            get() = Color.Red

    },
    Square {
        override val influentiality get() = 2
        override val glyph: Int?
            get() = null
        override val angle get() = 90.0
        override val color: Color
            get() = Color.Blue

    },
    Sextile {
        override val influentiality get() = 3
        override val glyph: Int?
            get() = null
        override val angle get() = 60.0
        override val color: Color
            get() = Color.Green
    },
    SemiSextile {
        override val influentiality get() = 3
        override val glyph: Int?
            get() = null
        override val angle get() = 30.0
        override val color: Color
            get() = Color.Yellow
    },
    Quincunx {
        override val influentiality get() = 4
        override val glyph: Int?
            get() = null
        override val angle get() = 150.0
        override val color: Color
            get() = Color.Gray
    },
    Quintile {
        override val influentiality get() = 4
        override val glyph: Int?
            get() = null
        override val angle get() = 72.0
        override val color: Color
            get() = Color.LightGray

    },
    Biquintile {
        override val influentiality get() = 4
        override val glyph: Int?
            get() = null
        override val angle get() = 144.0
        override val color: Color
            get() = Color.DarkGray

    },
    Octile {
        override val influentiality get() = 5
        override val glyph: Int?
            get() = null
        override val angle get() = 45.0
        override val color: Color
            get() = Color.Gray

    },
    Trioctile {
        override val influentiality get() = 5
        override val glyph: Int?
            get() = null
        override val angle get() = 135.0
        override val color: Color
            get() = Color.Gray

    },

    Decile {
        override val influentiality get() = 5
        override val glyph: Int?
            get() = null
        override val angle get() = 36.0
        override val color: Color
            get() = Color.Gray

    },

    Tridecile {
        override val influentiality get() = 5
        override val glyph: Int?
            get() = null
        override val angle get() = 108.0
        override val color: Color
            get() = Color.Gray

    };

    abstract val influentiality: Int
    abstract val glyph: Int?
    abstract val angle: Double
    abstract val color: Color
}
