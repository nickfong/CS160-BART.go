
bg = new BackgroundLayer
 	backgroundColor: "#FFFFFF"

mypage = new PageComponent
 	width:500
 	height:500
mypage.center()

layerA = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerAi = new Layer
	image:"images/screen1-round.png"
layerAi.width = 500
layerAi.height = 500
layerA.addSubLayer(layerAi)
layerAi.center()

layerB = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerBi = new Layer
    image:"images/screen2-round.png"
layerBi.width = 500
layerBi.height = 500
layerB.addSubLayer(layerBi)
layerBi.center()

layerC = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerCi = new Layer
    image:"images/screen3-round.png"
layerCi.width = 500
layerCi.height = 500
layerC.addSubLayer(layerCi)
layerCi.center()

layerD = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerDi = new Layer
    image:"images/screen4-round.png"
layerDi.width = 500
layerDi.height = 500
layerD.addSubLayer(layerDi)
layerDi.center()

layerE = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerEi = new Layer
    image:"images/screen5-round.png"
layerEi.width = 500
layerEi.height = 500
layerE.addSubLayer(layerEi)
layerEi.center()

layerE.on Events.Click, (event, layer) ->
    # layer.x = layer.x + 500
    layerA.x = layerA.x - 500
    layerB.x = layerB.x - 500
    layerC.x = layerC.x - 500
    layerD.x = layerD.x - 500
    layerF.x = layerF.x - 500
    layerG.x = layerG.x - 500

layerF = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerFi = new Layer
    image:"images/screen6-round.png"
layerFi.width = 500
layerFi.height = 500
layerF.addSubLayer(layerFi)
layerFi.center()

layerG = new Layer
 	width:500
 	height:500
 	backgroundColor:"#FFFFFF"
layerGi = new Layer
    image:"images/screen7-round.png"
layerGi.width = 500
layerGi.height = 500
layerG.addSubLayer(layerGi)
layerGi.center()

mypage.addPage(layerA)
mypage.addPage(layerB, "right")
mypage.addPage(layerC, "right")
mypage.addPage(layerD, "right")
mypage.addPage(layerE, "bottom")
mypage.addPage(layerF, "right")
mypage.addPage(layerG, "right")

layerE.x = layerD.x

moto360 = new Layer
	width: 975, height: 1455
	image: "images/moto_moto360-mask.png"
moto360.scaleX = 0.60
moto360.scaleY = 0.60
moto360.center()
moto360.x -= 4

#
# layerB.x = layerA.x
# layerC.x = layerB.x

# layerA.draggable.enabled = true
# layerA.draggable.horizontal = false
# layerA.draggable.constraints =
# 	x:100
# 	y:100
# 	width:120
# 	height:120
# # layerB.draggable.enabled = true
# layerB.draggable.horizontal = false
# layerB.draggable.constraints =
# 	x:100
# 	y:100
# 	width:120
# 	height:120
