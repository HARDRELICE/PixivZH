package com.hardrelice.pixivzh.ui.main.datatype

class SearchSetting(
    var by: String = BY_ARTWORKS,
    var order: String = ORDER_LATEST,
    var mode: String = "all",
    var p: Int = 1,
    var s_mode: String = SEARCH_MODE_TAG,
    var ratio: String = RATIO_ANY,
    var type: String = "all",
    var lang: String = "zh",
    var wlt: String = "",
    var hlt: String = "",
    var wgt: String = "",
    var hgt: String = "",
    var tool: String = "",
    var scd: String = "",
    var ecd: String = ""
) {
    companion object {
        const val BY_ARTWORKS = "artworks"
        const val BY_ILLUSTRATIONS = "illustrations"
        const val BY_MANGA = "manga"
        const val ILLUSTRATIONS_TYPE_ILLUST = "illust"
        const val ILLUSTRATIONS_TYPE_UGOIRA = "ugoira"
        const val ILLUSTRATIONS_TYPE_BOTH = "illust_and_ugoira"
        const val ORDER_LATEST = "date"
        const val ORDER_OLDEST = "date_d"
        const val SEARCH_MODE_TAG = "s_tag"
        const val SEARCH_MODE_TAG_FULL = "s_tag_full"
        const val SEARCH_MODE_TEXT_AND_CAPTION = "s_tc"
        const val RATIO_ANY = ""
        const val RATIO_SQUARE = "0"
        const val RATIO_HORIZONTAL = "0.5"
        const val RATIO_VERTICAL = "-0.5"
        val TOOLS = TOOLS()
    }

    class TOOLS {
        companion object {
            const val SAI = "SAI"
            const val Photoshop = "Photoshop"
            const val CLIP_STUDIO_PAINT = "CLIP STUDIO PAINT"
            const val IllustStudio = "IllustStudio"
            const val ComicStudio = "ComicStudio"
            const val Pixia = "Pixia"
            const val AzPainter4 = "AzPainter4"
            const val Painter = "Painter"
            const val Illustrator = "Illustrator"
            const val GIMP = "GIMP"
            const val FireAlpaca = "FireAlpaca"
            const val 網上描繪 = "網上描繪"
            const val AzPainter = "AzPainter"
            const val CGillust = "CGillust"
            const val 描繪聊天室 = "描繪聊天室"
            const val 手畫博克 = "手畫博克"
            const val MS_Paint = "MS_Paint"
            const val PictBear = "PictBear"
            const val openCanvas = "openCanvas"
            const val PaintShopPro = "PaintShopPro"
            const val EDGE = "EDGE"
            const val drawr = "drawr"
            const val COMICWORKS = "COMICWORKS"
            const val AzDrawing = "AzDrawing"
            const val SketchBookPro = "SketchBookPro"
            const val PhotoStudio = "PhotoStudio"
            const val Paintgraphic = "Paintgraphic"
            const val MediBang_Paint = "MediBang Paint"
            const val NekoPaint = "NekoPaint"
            const val Inkscape = "Inkscape"
            const val ArtRage = "ArtRage"
            const val AzDrawing4 = "AzDrawing4"
            const val Fireworks = "Fireworks"
            const val ibisPaint = "ibisPaint"
            const val AfterEffects = "AfterEffects"
            const val mdiapp = "mdiapp"
            const val GraphicsGale = "GraphicsGale"
            const val Krita = "Krita"
            const val kokuban_in = "kokuban.in"
            const val RETAS_STUDIO = "RETAS STUDIO"
            const val emote = "emote"
            const val _4thPaint = "4thPaint"
            const val ComiLabo = "ComiLabo"
            const val pixiv_Sketch = "pixiv Sketch"
            const val Pixelmator = "Pixelmator"
            const val Procreate = "Procreate"
            const val Expression = "Expression"
            const val PicturePublisher = "PicturePublisher"
            const val Processing = "Processing"
            const val Live2D = "Live2D"
            const val dotpict = "dotpict"
            const val Aseprite = "Aseprite"
            const val Poser = "Poser"
            const val Metasequoia = "Metasequoia"
            const val Blender = "Blender"
            const val Shade = "Shade"
            const val _3dsMax = "3dsMax"
            const val DAZ_Studio = "DAZ Studio"
            const val ZBrush = "ZBrush"
            const val Comi_Po = "Comi Po!"
            const val Maya = "Maya"
            const val Lightwave3D = "Lightwave3D"
            const val 六角大王 = "六角大王"
            const val Vue = "Vue"
            const val SketchUp = "SketchUp"
            const val CINEMA4D = "CINEMA4D"
            const val XSI = "XSI"
            const val CARRARA = "CARRARA"
            const val Bryce = "Bryce"
            const val STRATA = "STRATA"
            const val Sculptris = "Sculptris"
            const val modo = "modo"
            const val AnimationMaster = "AnimationMaster"
            const val VistaPro = "VistaPro"
            const val Sunny3D = "Sunny3D"
            const val _3D_Coat = "3D-Coat"
            const val Paint_3D = "Paint 3D"
            const val VRoid_Studio = "VRoid Studio"
            const val 筆芯筆 = "筆芯筆"
            const val 鉛筆 = "鉛筆"
            const val 原子筆 = "原子筆"
            const val 毫筆 = "毫筆"
            const val 顏色鉛筆 = "顏色鉛筆"
            const val Copic麥克筆 = "Copic麥克筆"
            const val 沾水筆 = "沾水筆"
            const val 透明水彩 = "透明水彩"
            const val 毛筆 = "毛筆"
            const val 記號筆 = "記號筆"
            const val 麥克筆 = "麥克筆"
            const val 水溶性彩色铅笔 = "水溶性彩色铅笔"
            const val 涂料 = "涂料"
            const val 丙烯顏料 = "丙烯顏料"
            const val 鋼筆 = "鋼筆"
            const val 粉彩 = "粉彩"
            const val 噴筆 = "噴筆"
            const val 顏色墨水 = "顏色墨水"
            const val 油彩 = "油彩"
            const val COUPY_PENCIL = "COUPY-PENCIL"
            const val 顏彩 = "顏彩"
            const val 蠟筆 = "蠟筆"
            val ALL = listOf(
                "所有的制图工具",
                "SAI",
                "Photoshop",
                "CLIP STUDIO PAINT",
                "IllustStudio",
                "ComicStudio",
                "Pixia",
                "AzPainter4",
                "Painter",
                "Illustrator",
                "GIMP",
                "FireAlpaca",
                "網上描繪",
                "AzPainter",
                "CGillust",
                "描繪聊天室",
                "手畫博克",
                "MS_Paint",
                "PictBear",
                "openCanvas",
                "PaintShopPro",
                "EDGE",
                "drawr",
                "COMICWORKS",
                "AzDrawing",
                "SketchBookPro",
                "PhotoStudio",
                "Paintgraphic",
                "MediBang Paint",
                "NekoPaint",
                "Inkscape",
                "ArtRage",
                "AzDrawing4",
                "Fireworks",
                "ibisPaint",
                "AfterEffects",
                "mdiapp",
                "GraphicsGale",
                "Krita",
                "kokuban.in",
                "RETAS STUDIO",
                "emote",
                "4thPaint",
                "ComiLabo",
                "pixiv Sketch",
                "Pixelmator",
                "Procreate",
                "Expression",
                "PicturePublisher",
                "Processing",
                "Live2D",
                "dotpict",
                "Aseprite",
                "Poser",
                "Metasequoia",
                "Blender",
                "Shade",
                "3dsMax",
                "DAZ Studio",
                "ZBrush",
                "Comi Po!",
                "Maya",
                "Lightwave3D",
                "六角大王",
                "Vue",
                "SketchUp",
                "CINEMA4D",
                "XSI",
                "CARRARA",
                "Bryce",
                "STRATA",
                "Sculptris",
                "modo",
                "AnimationMaster",
                "VistaPro",
                "Sunny3D",
                "3D-Coat",
                "Paint 3D",
                "VRoid Studio",
                "筆芯筆",
                "鉛筆",
                "原子筆",
                "毫筆",
                "顏色鉛筆",
                "Copic麥克筆",
                "沾水筆",
                "透明水彩",
                "毛筆",
                "記號筆",
                "麥克筆",
                "水溶性彩色铅笔",
                "涂料",
                "丙烯顏料",
                "鋼筆",
                "粉彩",
                "噴筆",
                "顏色墨水",
                "蠟筆",
                "油彩",
                "COUPY-PENCIL",
                "顏彩",
            )
        }
    }

}
