package com.servicebio.compose.application.emoji

import android.text.Spannable
import com.servicebio.compose.application.R
import java.util.regex.Pattern

class Emoji {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Emoji() }
    }
    private val _emojiIcons = ArrayList<EmojiModel>(100)
    val emojiIcons
        get() = _emojiIcons.toList()

    val emojiIconMap = LinkedHashMap<String, Int>(100)

    init {
        emojiIconMap["[微笑]"] = R.drawable.img001
        emojiIconMap["[撇嘴]"] = R.drawable.img002
        emojiIconMap["[色]"] = R.drawable.img003
        emojiIconMap["[发呆]"] = R.drawable.img004
        emojiIconMap["[得意]"] = R.drawable.img005
        emojiIconMap["[流泪]"] = R.drawable.img006
        emojiIconMap["[害羞]"] = R.drawable.img007
        emojiIconMap["[闭嘴]"] = R.drawable.img008
        emojiIconMap["[笑脸]"] = R.drawable.img009
        emojiIconMap["[大哭]"] = R.drawable.img010
        emojiIconMap["[尴尬]"] = R.drawable.img011
        emojiIconMap["[发怒]"] = R.drawable.img012
        emojiIconMap["[调皮]"] = R.drawable.img013
        emojiIconMap["[呲牙]"] = R.drawable.img014
        emojiIconMap["[惊讶]"] = R.drawable.img015
        emojiIconMap["[难过]"] = R.drawable.img016
        emojiIconMap["[酷]"] = R.drawable.img017
        emojiIconMap["[冷汗]"] = R.drawable.img018
        emojiIconMap["[抓狂]"] = R.drawable.img019
        emojiIconMap["[吐]"] = R.drawable.img020
        emojiIconMap["[偷笑]"] = R.drawable.img021
        emojiIconMap["[愉快]"] = R.drawable.img022
        emojiIconMap["[白眼]"] = R.drawable.img023
        emojiIconMap["[傲慢]"] = R.drawable.img024
        emojiIconMap["[困]"] = R.drawable.img025
        emojiIconMap["[惊恐]"] = R.drawable.img026
        emojiIconMap["[流汗]"] = R.drawable.img027
        emojiIconMap["[憨笑]"] = R.drawable.img028
        emojiIconMap["[悠闲]"] = R.drawable.img029
        emojiIconMap["[奋斗]"] = R.drawable.img030
        emojiIconMap["[咒骂]"] = R.drawable.img031
        emojiIconMap["[疑问]"] = R.drawable.img032
        emojiIconMap["[嘘]"] = R.drawable.img033
        emojiIconMap["[晕]"] = R.drawable.img034
        emojiIconMap["[爱心]"] = R.drawable.img035
        emojiIconMap["[生病]"] = R.drawable.img036
        emojiIconMap["[敲打]"] = R.drawable.img037
        emojiIconMap["[再见]"] = R.drawable.img038
        emojiIconMap["[擦汗]"] = R.drawable.img039
        emojiIconMap["[抠鼻]"] = R.drawable.img040
        emojiIconMap["[糗大了]"] = R.drawable.img041
        emojiIconMap["[坏笑]"] = R.drawable.img042
        emojiIconMap["[左哼哼]"] = R.drawable.img043
        emojiIconMap["[右哼哼]"] = R.drawable.img044
        emojiIconMap["[哈欠]"] = R.drawable.img045
        emojiIconMap["[鄙视]"] = R.drawable.img046
        emojiIconMap["[委屈]"] = R.drawable.img047
        emojiIconMap["[快哭了]"] = R.drawable.img048
        emojiIconMap["[阴险]"] = R.drawable.img049
        emojiIconMap["[亲亲]"] = R.drawable.img050
        emojiIconMap["[吓]"] = R.drawable.img051
        emojiIconMap["[可怜]"] = R.drawable.img052
        emojiIconMap["[笑哭了]"] = R.drawable.img053
        emojiIconMap["[装B失败]"] = R.drawable.img054
        emojiIconMap["[折磨]"] = R.drawable.img055
        emojiIconMap["[庆祝]"] = R.drawable.img056
        emojiIconMap["[西瓜]"] = R.drawable.img057
        emojiIconMap["[脸红]"] = R.drawable.img058
        emojiIconMap["[恐惧]"] = R.drawable.img059
        emojiIconMap["[失望]"] = R.drawable.img060
        emojiIconMap["[破涕为笑]"] = R.drawable.img061
        emojiIconMap["[无语]"] = R.drawable.img062
        emojiIconMap["[嘿哈]"] = R.drawable.img063
        emojiIconMap["[捂脸]"] = R.drawable.img064
        emojiIconMap["[奸笑]"] = R.drawable.img065
        emojiIconMap["[机智]"] = R.drawable.img066
        emojiIconMap["[皱眉]"] = R.drawable.img067
        emojiIconMap["[耶]"] = R.drawable.img068
        emojiIconMap["[开黑]"] = R.drawable.img069
        emojiIconMap["[吼吼吼]"] = R.drawable.img070
        emojiIconMap["[打脸]"] = R.drawable.img071
        emojiIconMap["[摸鱼]"] = R.drawable.img072
        emojiIconMap["[啥事]"] = R.drawable.img073
        emojiIconMap["[发财啦]"] = R.drawable.img074
        emojiIconMap["[吐血]"] = R.drawable.img075
        emojiIconMap["[摸摸头]"] = R.drawable.img076
        emojiIconMap["[可了]"] = R.drawable.img077
        emojiIconMap["[欣喜]"] = R.drawable.img078
        emojiIconMap["[你瞅啥]"] = R.drawable.img079
        emojiIconMap["[拥抱]"] = R.drawable.img080
        emojiIconMap["[狗]"] = R.drawable.img081
        emojiIconMap["[干嘛鸭]"] = R.drawable.img082
        emojiIconMap["[鸽王]"] = R.drawable.img083
        emojiIconMap["[酸了]"] = R.drawable.img084
        emojiIconMap["[蒜了]"] = R.drawable.img085
        emojiIconMap["[菜]"] = R.drawable.img086
        emojiIconMap["[鬼魂]"] = R.drawable.img087
        emojiIconMap["[咕咕咕]"] = R.drawable.img088
        emojiIconMap["[猪头]"] = R.drawable.img089
        emojiIconMap["[辣鸡]"] = R.drawable.img090
        emojiIconMap["[睡]"] = R.drawable.img091
        emojiIconMap["[玫瑰]"] = R.drawable.img092
        emojiIconMap["[凋谢]"] = R.drawable.img093
        emojiIconMap["[吻]"] = R.drawable.img094
        emojiIconMap["[心碎]"] = R.drawable.img095
        emojiIconMap["[衰]"] = R.drawable.img096
        emojiIconMap["[骷髅]"] = R.drawable.img097
        emojiIconMap["[便便]"] = R.drawable.img098
        emojiIconMap["[月亮]"] = R.drawable.img099
        emojiIconMap["[太阳]"] = R.drawable.img100
        emojiIconMap["[爱你]"] = R.drawable.img101
        emojiIconMap["[强]"] = R.drawable.img102
        emojiIconMap["[弱]"] = R.drawable.img103
        emojiIconMap["[握手]"] = R.drawable.img104
        emojiIconMap["[胜利]"] = R.drawable.img105
        emojiIconMap["[抱拳]"] = R.drawable.img106
        emojiIconMap["[勾引]"] = R.drawable.img107
        emojiIconMap["[拳头]"] = R.drawable.img108
        emojiIconMap["[OK]"] = R.drawable.img109
        emojiIconMap["[NO]"] = R.drawable.img110
        emojiIconMap["[强壮]"] = R.drawable.img111
        emojiIconMap["[合十]"] = R.drawable.img112
        emojiIconMap["[鼓掌]"] = R.drawable.img113
        emojiIconMap["[啤酒]"] = R.drawable.img114
        emojiIconMap["[咖啡]"] = R.drawable.img115
        emojiIconMap["[菜刀]"] = R.drawable.img116
        emojiIconMap["[炸弹]"] = R.drawable.img117
        emojiIconMap["[礼物]"] = R.drawable.img118
        emojiIconMap["[红包]"] = R.drawable.img119
        emojiIconMap["[蛋糕]"] = R.drawable.img120

        val sets: Set<Map.Entry<String, Int>> =
            emojiIconMap.entries

        sets.forEach {
            _emojiIcons.add(EmojiModel(it.value, it.key))
        }
    }


    private val pattern = "^(\\[(\\w|[\\u4e00-\\u9fa5]){1,4}\\])"
    private val mPattern = Pattern.compile(pattern)
    private val mMatcher = mPattern.matcher("")


      fun find(text: Spannable, startIndex: Int): EmojiModel? {
        if (text[startIndex] == '[') {
            mMatcher.reset(text.subSequence(startIndex, text.length))
            if (mMatcher.find()) {
                val match: String = mMatcher.group()
                return emojiIconMap[match]?.let {
                    EmojiModel(it, match)
                }
            }
        }
        return null
    }

}