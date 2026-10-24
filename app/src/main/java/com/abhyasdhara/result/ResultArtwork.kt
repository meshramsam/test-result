package com.abhyasdhara.result

import android.content.Context
import android.graphics.*
import kotlin.math.*

/** All geometry uses the supplied 1122 × 1402 artwork as its coordinate space.
 * No screenshot or web surface is embedded: surfaces, symbols and lettering are native draws. */
internal class ResultArtwork(context: Context) {
    private val regular = context.resources.getFont(R.font.roboto)
    private val devanagari = context.resources.getFont(R.font.noto_devanagari)
    private lateinit var c: Canvas
    private val navy = "#0B143C"
    private val muted = "#53617D"
    private fun color(s: String) = Color.parseColor(s)
    private fun paint(s: String) = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = color(s) }
    private fun path(block: Path.() -> Unit) = Path().apply(block)
    private fun rounded(x: Float,y: Float,w: Float,h: Float,r: Float) = path {
        addRoundRect(RectF(x,y,x+w,y+h),r,r,Path.Direction.CW)
    }
    private fun gradient(x: Float,y: Float,x2: Float,y2: Float,vararg colors: String): Shader =
        LinearGradient(x,y,x2,y2,colors.map(::color).toIntArray(),null,Shader.TileMode.CLAMP)
    private fun fill(p: Path, s: String) = c.drawPath(p,paint(s))
    private fun fill(p: Path, shader: Shader) = c.drawPath(p,Paint(3).apply { this.shader=shader })
    private fun stroke(p: Path,s: String,width: Float=1f) = c.drawPath(p,paint(s).apply {
        style=Paint.Style.STROKE; strokeWidth=width; strokeCap=Paint.Cap.ROUND; strokeJoin=Paint.Join.ROUND
    })
    private fun shadow(p: Path,blur: Float,dx: Float,dy: Float,s: String) {
        c.drawPath(p,paint("#FF000000").apply { color=Color.TRANSPARENT; setShadowLayer(blur,dx,dy,color(s)) })
    }
    private fun circle(x: Float,y: Float,r: Float,s: String) = c.drawCircle(x,y,r,paint(s))
    private fun disc(x: Float,y: Float,r: Float) = path { addCircle(x,y,r,Path.Direction.CW) }
    private fun line(x: Float,y: Float,x2: Float,y2: Float,s: String,width: Float=1f) =
        stroke(path { moveTo(x,y); lineTo(x2,y2) },s,width)
    private fun text(t: String,x: Float,y: Float,size: Float,s: String=navy,weight: Int=500,
                     center: Boolean=false,dev: Boolean=false, spacing: Float=0f) {
        val p=paint(s).apply {
            textSize=size; typeface=Typeface.create(if(dev) devanagari else regular,weight,false)
            textAlign=if(center) Paint.Align.CENTER else Paint.Align.LEFT
        }
        if(spacing==0f) c.drawText(t,x,y,p) else {
            val widths=t.map { p.measureText(it.toString()) }
            var start=if(center) x-(widths.sum()+spacing*(t.length-1))/2 else x
            t.forEachIndexed { i,ch -> c.drawText(ch.toString(),start,y,p); start+=widths[i]+spacing }
        }
    }
    private fun raisedText(t: String,x: Float,y: Float,size: Float,weight: Int=700,center: Boolean=false) {
        text(t,x+1,y+2,size,"#80071328",weight,center)
        text(t,x,y,size,"#F4F7FC",weight,center)
    }
    fun render(width: Int,height: Int): Bitmap {
        val bitmap=Bitmap.createBitmap(width.coerceAtLeast(1),height.coerceAtLeast(1),Bitmap.Config.ARGB_8888)
        c=Canvas(bitmap); c.scale(width/1122f,height/1402f)
        background(); header(); score(); statistics(); duration(); performance()
        return bitmap
    }
    private fun background() {
        c.drawColor(color("#FAFBFC"))
        fill(rounded(0f,0f,1122f,1402f,0f),gradient(0f,700f,1122f,700f,"#FDFDFD","#F1F3F5","#FDFEFE"))
        val body=rounded(154f,21f,811f,1344f,73f)
        shadow(body,38f,-12f,32f,"#65757D84")
        shadow(body,10f,-5f,8f,"#38605A50")
        fill(body,gradient(154f,400f,964f,700f,"#DEDCD8","#FAFAFA","#DCE2EC"))
        stroke(body,"#93FFFFFF",1.6f)
        val face=rounded(174f,32f,785f,1312f,63f)
        shadow(face,5f,-2f,1f,"#376F6A62")
        fill(face,gradient(180f,45f,870f,1344f,"#FEFEFE","#F4F4F3","#F0F2F5","#D9DCDD"))
        stroke(face,"#EFFFFFFF",2f)
        // Rounded outer lip catches the upper-left light; the bottom rim is shaded.
        stroke(path { moveTo(164f,1268f); lineTo(164f,91f); cubicTo(164f,47f,189f,27f,229f,27f); lineTo(886f,27f); cubicTo(929f,27f,958f,55f,958f,96f) },"#BCFFFFFF",7f)
        fill(path { moveTo(807f,34f); cubicTo(892f,31f,947f,74f,958f,160f); lineTo(958f,94f); cubicTo(958f,55f,929f,32f,890f,32f); close() },"#70FFFFFF")
    }
    private fun header() {
        val logo=disc(270f,105f,57f)
        shadow(logo,12f,-4f,12f,"#50716E68")
        fill(logo,gradient(225f,50f,309f,161f,"#34648C","#153754")); stroke(logo,"#F9FCFF",2f)
        stroke(disc(270f,105f,55f),"#6D99BD",1.5f)
        // Open book and flame, traced as independent gold surfaces.
        fill(path { moveTo(270f,120f); cubicTo(263f,110f,251f,113f,244f,111f); lineTo(244f,88f); cubicTo(261f,90f,267f,100f,270f,111f); close() },gradient(244f,85f,273f,120f,"#FFF0B6","#DCAF54"))
        fill(path { moveTo(273f,120f); cubicTo(281f,112f,289f,113f,297f,111f); lineTo(297f,88f); cubicTo(282f,89f,275f,101f,273f,111f); close() },gradient(272f,86f,299f,121f,"#FFF0B6","#E1B95E"))
        val flame=path { moveTo(270f,70f); cubicTo(258f,85f,265f,91f,270f,97f); cubicTo(280f,85f,276f,80f,270f,70f); close() }
        fill(flame,gradient(264f,70f,275f,98f,"#FFF4C7","#E9B749")); stroke(flame,"#FFF0B3",1f)
        for(side in listOf(-1,1)) {
            stroke(path { moveTo(270f,139f); cubicTo(270f+side*25f,142f,270f+side*46f,123f,270f+side*43f,100f) },"#D8AC4F",1.5f)
            for(i in 0..5) {
                val a=(18+i*12)*PI/180
                val x=270f+side*(44*cos(a)).toFloat(); val y=99f+(39*sin(a)).toFloat()
                c.save(); c.rotate(side*(i*12f-18f),x,y)
                fill(path { moveTo(x,y); quadTo(x-side*8,y-9,x-side*1,y-13); quadTo(x+side*5,y-7,x,y); close() },"#EBC66D")
                fill(path { moveTo(x,y+3); quadTo(x+side*11,y-5,x+side*10,y+1); quadTo(x+side*6,y+7,x,y+3); close() },"#E9BC58")
                c.restore()
            }
        }
        val badge=rounded(354f,62f,332f,66f,34f)
        fill(badge,gradient(355f,62f,670f,135f,"#2464A5","#1C3C61","#172D47")); stroke(badge,"#122E52",1.5f)
        stroke(path { moveTo(383f,64f); lineTo(651f,64f) },"#7296B2",1f)
        bars(389f,81f,0.73f,"#D8EBFF")
        text("TEST RESULT",547f,105f,29f,"#F5F5FD",800,true,spacing=2.5f)
        text("अभ्यासधारा अकॅडमी – प्रीमियम साप्ताहिक",596f,182f,38f,weight=750,center=true,dev=true)
        text("पोलीस भरती टेस्ट",586f,228f,38f,weight=750,center=true,dev=true)
        text("(१७ ते २४ ऑक्टोबर २०२६)",585f,271f,31f,weight=450,center=true,dev=true)
        calendar(427f,295f)
        text("Test Date : 17 October 2026",463f,314f,23f,muted,500)
    }
    private fun score() {
        val outer=path {
            moveTo(174f,385f); cubicTo(170f,329f,219f,291f,276f,294f)
            cubicTo(339f,292f,378f,330f,451f,346f)
            cubicTo(560f,372f,705f,354f,816f,337f)
            cubicTo(880f,327f,913f,326f,934f,363f)
            cubicTo(947f,383f,946f,399f,946f,421f)
            lineTo(946f,615f); cubicTo(946f,658f,925f,676f,887f,676f)
            lineTo(747f,676f); cubicTo(695f,675f,660f,655f,636f,622f)
            cubicTo(642f,663f,617f,690f,579f,691f)
            lineTo(243f,691f); cubicTo(194f,691f,174f,656f,174f,612f); close()
        }
        shadow(outer,19f,0f,19f,"#56616C73")
        fill(outer,gradient(230f,290f,760f,711f,"#527A9D","#234A70","#0B283F"))
        stroke(outer,"#EDF9FF",2f)
        val front=path {
            moveTo(177f,383f); cubicTo(177f,327f,224f,295f,275f,298f)
            cubicTo(337f,297f,379f,335f,451f,350f)
            cubicTo(565f,375f,709f,357f,819f,340f)
            cubicTo(883f,330f,911f,329f,932f,367f)
            cubicTo(942f,385f,940f,402f,940f,422f); lineTo(940f,612f)
            cubicTo(940f,653f,924f,669f,886f,669f); lineTo(747f,669f)
            cubicTo(698f,671f,667f,650f,641f,619f)
            cubicTo(612f,576f,568f,559f,499f,559f)
            lineTo(292f,559f); cubicTo(236f,559f,207f,528f,207f,475f)
            lineTo(207f,405f); cubicTo(207f,377f,224f,354f,249f,349f)
            cubicTo(207f,356f,177f,371f,177f,409f); close()
        }
        fill(front,gradient(315f,303f,751f,681f,"#294D72","#173859","#0B2946","#172F46"))
        // Broad, restrained light falloff on the score face.
        c.save(); c.clipPath(front)
        c.drawCircle(495f,346f,410f,Paint(3).apply { shader=RadialGradient(495f,346f,410f,intArrayOf(color("#173F729F"),Color.TRANSPARENT),null,Shader.TileMode.CLAMP) }); c.restore()
        stroke(path { moveTo(178f,378f); cubicTo(178f,327f,224f,295f,275f,298f); cubicTo(337f,297f,379f,335f,451f,350f); cubicTo(565f,375f,709f,357f,819f,340f); cubicTo(883f,330f,911f,329f,932f,367f) },"#81AED7",2f)
        val accuracy=path { moveTo(207f,434f); lineTo(207f,476f); cubicTo(207f,531f,236f,559f,292f,559f); lineTo(499f,559f); cubicTo(561f,559f,602f,574f,625f,610f); cubicTo(652f,650f,617f,686f,579f,686f); lineTo(245f,686f); cubicTo(196f,686f,178f,653f,178f,609f); lineTo(178f,420f); cubicTo(178f,390f,190f,371f,218f,359f); close() }
        fill(accuracy,gradient(220f,520f,545f,722f,"#193C5D","#23415E","#0B2438"))
        stroke(path { moveTo(207f,433f); lineTo(207f,477f); cubicTo(207f,532f,238f,559f,292f,559f); lineTo(499f,559f); cubicTo(561f,559f,602f,574f,625f,610f); cubicTo(652f,650f,617f,686f,579f,686f) },"#64A2D3",3f)
        stroke(path { moveTo(208f,477f); cubicTo(208f,531f,238f,557f,292f,557f); lineTo(499f,557f) },"#0E223A",1.5f)
        medallion(263f,393f,43f,false); trophy(242f,373f)
        text("OBTAINED MARKS",327f,401f,23f,"#BFD3EC",700,spacing=.5f)
        raisedText("0",300f,531f,138f,800)
        text("/",408f,523f,77f,"#CAD8EE",400)
        text("100",452f,523f,89f,"#080F25",750)
        text("100",451f,522f,89f,"#C7DBF6",750)
        medallion(266f,623f,46f,true); target(266f,623f)
        text("ACCURACY",345f,606f,24f,"#B5CAE1",600,spacing=.6f)
        raisedText("0%",345f,663f,55f)
        val ring=disc(786f,512f,137f)
        fill(ring,gradient(670f,380f,886f,648f,"#19334E","#203E5C","#122A43")); stroke(ring,"#0E2946",2f)
        stroke(path { addArc(RectF(649f,375f,923f,649f),15f,175f) },"#315C80",2f)
        val rim=disc(786f,511f,120f)
        fill(rim,gradient(704f,399f,877f,629f,"#526F8C","#3D536D")); stroke(rim,"#7089A1",1f)
        val inside=disc(786f,511f,101f)
        fill(inside,gradient(706f,419f,858f,601f,"#254B6B","#193049")); stroke(inside,"#102B45",1.5f)
        shadow(disc(787f,399f,10f),4f,1f,2f,"#680C213B")
        fill(disc(787f,399f,10f),gradient(778f,390f,795f,407f,"#FAFFFF","#B6DDFF")); stroke(disc(787f,399f,10f),"#F6FCFF",1f)
        raisedText("0",747f,528f,80f,750); raisedText("%",796f,527f,49f,650)
        text("PERCENTAGE",789f,562f,19f,"#C4D5EF",650,true,spacing=.6f)
    }
    private fun panel(x: Float,y: Float,w: Float,h: Float,r: Float,vararg colors: String) {
        val p=rounded(x,y,w,h,r)
        shadow(p,14f,-1f,13f,"#34665D52")
        fill(p,gradient(x,y,x+w,y+h,*colors)); stroke(p,"#BEFFFFFF",3f)
        stroke(path { moveTo(x+r,y+2); lineTo(x+w-r,y+2) },"#D9FFFFFF",1.5f)
    }
    private fun statistics() {
        panel(178f,702f,763f,291f,51f,"#F7F7F7","#F6F6F5","#EBEDEF")
        line(452f,727f,452f,823f,"#C5C8CD",1.2f); line(707f,727f,707f,823f,"#C5C8CD",1.2f)
        line(213f,849f,912f,849f,"#C7C9CD",1.3f); line(563f,876f,563f,970f,"#C5C8CD",1.2f)
        circle(253f,767f,41f,"#D4E3F4"); document(238f,749f)
        label("Total","Questions","100",315f,750f)
        circle(517f,767f,41f,"#D9EBDD"); check(517f,768f)
        label("Correct","Answers","0",574f,750f)
        circle(773f,767f,41f,"#F5DEDE"); cross(773f,768f)
        label("Wrong","Answers","0",829f,750f)
        circle(253f,916f,41f,"#F8E6CB"); skip(239f,902f)
        label("Skipped","Questions","100",315f,898f)
        circle(639f,916f,41f,"#DFDFFB"); person(639f,916f)
        label("Attempted","Questions","0",701f,898f)
    }
    private fun label(a: String,b: String,value: String,x: Float,y: Float) {
        text(a,x,y,22f,muted); text(b,x,y+28,22f,muted); text(value,x,y+70,37f,navy,750)
    }
    private fun duration() {
        panel(178f,1014f,763f,141f,48f,"#E7EBF5","#E8EBEE","#E5E9F7")
        line(573f,1041f,573f,1126f,"#929EB0",1.2f)
        circle(253f,1083f,42f,"#C5DAF8"); clock(253f,1083f)
        text("Total Test Duration",324f,1072f,23f,"#425677",550)
        text("90 minutes",324f,1112f,35f,navy,750)
        circle(659f,1083f,42f,"#CED5F1"); hourglass(659f,1083f)
        text("Time Taken",731f,1072f,23f,"#425677",550)
        text("2 seconds",731f,1112f,35f,navy,750)
    }
    private fun performance() {
        panel(178f,1173f,761f,153f,58f,"#F4E6E5","#F3E2DF","#EFDFDD")
        val p=disc(261f,1251f,48f)
        shadow(p,11f,0f,8f,"#286A5753")
        fill(p,gradient(237f,1208f,284f,1296f,"#FF9D9F","#E86C70","#B73137")); stroke(p,"#FFF0E9",1.5f)
        stroke(path { addArc(RectF(215f,1205f,307f,1297f),198f,125f) },"#FFB4B2",2f)
        fill(path { moveTo(229f,1267f); cubicTo(227f,1247f,238f,1238f,246f,1238f); cubicTo(251f,1225f,262f,1227f,267f,1232f); lineTo(287f,1265f); close() },"#245F1928")
        bars(242f,1231f,1f,"#FFF5F0")
        text("Performance Status",343f,1229f,24f,"#80515B",550)
        text("Needs More Practice",342f,1280f,40f,"#960E1D",750)
        text("Needs More Practice",342f,1278f,40f,"#B81524",750)
    }
    private fun medallion(x: Float,y: Float,r: Float,bright: Boolean) {
        val p=disc(x,y,r); shadow(p,9f,-3f,6f,"#46071426")
        fill(p,gradient(x-r,y-r,x+r,y+r,if(bright) "#3974A4" else "#315473","#1D3855"))
        stroke(p,"#0C2945",1.6f)
        stroke(path { addArc(RectF(x-r+1,y-r+1,x+r-1,y+r-1),185f,195f) },if(bright) "#62B5EB" else "#426F95",1.4f)
    }
    private fun bars(x: Float,y: Float,s: Float,tint: String) {
        for(i in 0..2) {
            val h=(16f+i*11)*s; val p=rounded(x+i*14*s,y+38*s-h,9*s,h,2*s)
            shadow(p,1f,1f,1f,"#800D2443"); fill(p,tint); stroke(p,"#FFFFFF",.8f)
        }
    }
    private fun calendar(x: Float,y: Float) {
        stroke(rounded(x,y,22f,21f,1.5f),"#405572",2.5f)
        line(x,y+5,x+22,y+5,"#405572",2f)
        line(x+5,y-2,x+5,y+3,"#405572",3f); line(x+17,y-2,x+17,y+3,"#405572",3f)
        for(row in 0..1) for(col in 0..2) fill(rounded(x+4+col*6,y+9+row*6,3f,3f,.5f),"#405572")
    }
    private fun trophy(x: Float,y: Float) {
        val handles=path { moveTo(x+9,y+5); lineTo(x+1,y+5); cubicTo(x-2,y+18,x+7,y+22,x+13,y+23); moveTo(x+28,y+5); lineTo(x+40,y+5); cubicTo(x+43,y+18,x+34,y+22,x+27,y+23) }
        stroke(handles,"#FFE18B",2.6f)
        val cup=path { moveTo(x+8,y); lineTo(x+33,y); lineTo(x+32,y+14); cubicTo(x+30,y+36,x+11,y+36,x+9,y+14); close() }
        fill(cup,gradient(x+8,y,x+33,y,"#FFE392","#F8C454","#FFF0AF")); stroke(cup,"#FFF0B2",1.2f)
        fill(path { moveTo(x+18,y+29); lineTo(x+24,y+29); lineTo(x+25,y+37); lineTo(x+31,y+39); lineTo(x+31,y+43); lineTo(x+9,y+43); lineTo(x+9,y+39); lineTo(x+16,y+37); close() },"#F3C25B")
        line(x+9,y+43,x+32,y+43,"#FFEA9D",1.7f)
    }
    private fun target(x: Float,y: Float) {
        shadow(disc(x-2,y+1,19f),2f,1f,2f,"#7020354E")
        stroke(disc(x-2,y+1,19f),"#F0F5FD",4f); stroke(disc(x-2,y+1,12f),"#F0F5FD",3f)
        circle(x-2,y+1,4f,"#EAF3FC")
        line(x-2,y+1,x+18,y-20,"#122D48",6f); line(x-2,y+1,x+18,y-20,"#F6F8FD",3.5f)
        stroke(path { moveTo(x+15,y-21); lineTo(x+15,y-15); lineTo(x+21,y-15) },"#F6F8FD",3f)
    }
    private fun document(x: Float,y: Float) {
        val p=path { moveTo(x+3,y); lineTo(x+21,y); lineTo(x+30,y+10); lineTo(x+30,y+36); quadTo(x+30,y+38,x+27,y+38); lineTo(x+2,y+38); quadTo(x,y+38,x,y+35); lineTo(x,y+3); quadTo(x,y,x+3,y); close() }
        fill(p,gradient(x,y,x+28,y+36,"#278CEB","#075EBA")); stroke(p,"#0054A2",1.3f)
        stroke(path { moveTo(x+20,y+1); lineTo(x+20,y+10); lineTo(x+29,y+10) },"#C8E9FF",2f)
        line(x+6,y+13,x+12,y+13,"#EDFAFF",2.5f)
        line(x+6,y+20,x+23,y+20,"#EDFAFF",2.5f)
        line(x+6,y+28,x+17,y+28,"#EDFAFF",2.5f)
    }
    private fun check(x: Float,y: Float) {
        fill(disc(x,y,19f),gradient(x-15,y-18,x+16,y+19,"#58B76C","#208346")); stroke(disc(x,y,19f),"#197641",1.2f)
        stroke(disc(x,y,20.5f),"#EFFFF1",1f)
        stroke(path { moveTo(x-9,y); lineTo(x-2,y+7); lineTo(x+10,y-8) },"#F2FFF2",4f)
    }
    private fun cross(x: Float,y: Float) {
        fill(disc(x,y,19f),gradient(x-14,y-18,x+14,y+20,"#F54736","#D72019")); stroke(disc(x,y,19f),"#B52421",1f); stroke(disc(x,y,21f),"#FFFFFF",1f)
        line(x-7,y-7,x+7,y+7,"#FFF7EF",3.5f); line(x+7,y-7,x-7,y+7,"#FFF7EF",3.5f)
    }
    private fun skip(x: Float,y: Float) {
        val p=path { moveTo(x,y+1); quadTo(x,y-2,x+3,y); lineTo(x+19,y+12); quadTo(x+22,y+14,x+19,y+17); lineTo(x+3,y+28); quadTo(x,y+30,x,y+26); close() }
        fill(p,gradient(x,y,x+20,y+28,"#F3B539","#CD7D0B")); stroke(p,"#FFF7DC",1.5f)
        fill(rounded(x+19,y-1,8f,30f,3f),gradient(x+19,y,x+27,y+30,"#EAA222","#CD7909")); stroke(rounded(x+19,y-1,8f,30f,3f),"#FFF4D2",1f)
    }
    private fun person(x: Float,y: Float) {
        val head=path { addOval(RectF(x-8,y-19,x+8,y-1),Path.Direction.CW) }
        fill(head,gradient(x-8,y-19,x+8,y,"#6477E3","#394DC4")); stroke(head,"#2439AC",1f)
        val body=path { moveTo(x-16,y+20); cubicTo(x-18,y-2,x+18,y-2,x+17,y+20); close() }
        fill(body,gradient(x,y+3,x,y+20,"#667BE4","#3C50BE")); stroke(body,"#263CB0",1f)
        stroke(path { moveTo(x-18,y+20); lineTo(x+18,y+20) },"#FAFAFF",1.2f)
    }
    private fun clock(x: Float,y: Float) {
        fill(disc(x,y,20f),gradient(x-15,y-18,x+16,y+19,"#318FFF","#1670D9")); stroke(disc(x,y,20f),"#0562D6",1f); stroke(disc(x,y,21.5f),"#E8F3FF",1f)
        stroke(path { moveTo(x,y-11); lineTo(x,y+1); lineTo(x+7,y+1) },"#EBF6FF",3f)
    }
    private fun hourglass(x: Float,y: Float) {
        val p=path { moveTo(x-12,y-19); cubicTo(x-12,y-8,x-7,y-6,x,y); cubicTo(x-7,y+7,x-12,y+9,x-12,y+19); lineTo(x+12,y+19); cubicTo(x+12,y+8,x+7,y+6,x,y); cubicTo(x+7,y-6,x+12,y-9,x+12,y-19); close() }
        stroke(p,"#F4F7FF",5f); stroke(p,"#334B9A",3.7f)
        line(x-13,y-19,x+13,y-19,"#344D9F",5f); line(x-13,y+20,x+13,y+20,"#344D9F",5f)
        fill(path { moveTo(x-8,y-8); lineTo(x+8,y-8); lineTo(x,y-1); close() },"#4665BF")
        fill(path { moveTo(x-8,y+17); quadTo(x-7,y+12,x,y+8); quadTo(x+7,y+12,x+8,y+17); close() },"#4665BF")
        line(x,y+1,x,y+10,"#4665BF",1f)
    }
}
