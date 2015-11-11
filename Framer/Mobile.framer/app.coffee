### Variables ###
stations = [
    "12th St. Oakland City Center",
    "16th St. Mission (SF)",
    "19th St. Oakland",
    "24th St. Mission (SF)",
    "Ashby (Berkeley)",
    "Balboa Park (SF)",
    "Bay Fair (San Leandro)",
    "Castro Valley",
    "Civic Center/UN Plaza (SF)",
    "Coliseum",
    "Colma",
    "Concord",
    "Daly City",
    "Downtown Berkeley",
    "Dublin/Pleasanton",
    "El Cerrito del Norte",
    "El Cerrito Plaza",
    "Embarcadero (SF)",
    "Fremont",
    "Fruitvale (Oakland)",
    "Glen Park (SF)",
    "Hayward",
    "Lafayette",
    "Lake Merritt (Oakland)",
    "MacArthur (Oakland)",
    "Millbrae",
    "Montgomery St. (SF)",
    "North Berkeley",
    "North Concord/Martinez",
    "Oakland Airport",
    "Orinda",
    "Pittsburg/Bay Point",
    "Pleasant Hill",
    "Powell St. (SF)",
    "Richmond",
    "Rockridge (Oakland)",
    "San Bruno",
    "San Francisco Airport",
    "San Leandro",
    "South Hayward",
    "South San Francisco",
    "Union City",
    "Walnut Creek",
    "West Dublin/Pleasanton",
    "West Oakland",
]

selectedLayer = false			# Pointer to the previously selected button
selectionDisabled = false				# If selection should be disabled
navEnabled = false				# If navigation is currently enabled

### Layers ###
bg = new BackgroundLayer
    backgroundColor: "#333"

title = new Layer
	x:-1
	y:50
	width: 425
	height: 120
	backgroundColor:"#3c9eff"
	html:"BART.go"
	style:
		"padding-top":"50px"
		"padding-left":"10px"
		"font-size":"100px"
		"font-weight":"bold"
		"font-variant":"small-caps"
		
help = new Layer
	x:0
	y:200
	width:Screen.width
	height:100
	backgroundColor: "#3c9eff"
	html:"Select your destination station:"
	style:
		"padding-top":"35px"
		"padding-left":"10px"
		"font-size":"60px"
		"font-weight":"bold"
help.centerX()
help.states.add
	blur: {blur:20}
	unblur: {blur:0}

home = new Layer
	x:0
	y:440
	height:100
	width:Screen.width - 20
	backgroundColor: "#dddddd"
	html: "Return to Home Station (Downtown Berkeley)"
	style: 
		"font-size":"40px"
		"text-align":"center"
		"padding":"35px"
		"color":"#111111"
		"font-weight":"bold"
home.centerX()
home.on Events.Click, (event, layer) ->
		switchSelection(this, layer)
home.states.add
	blur: {blur:20}
	unblur: {blur:0}

ret = new Layer
	x:0
	y:320
	height:100
	width:Screen.width - 20
	backgroundColor: "#dddddd"
	html: "Return to Last Embarcation Station (Millbrae)"
	style: 
		"font-size":"40px"
		"text-align":"center"	
		"padding":"35px"
		"color":"#111111"
		"font-weight":"bold"
ret.centerX()
ret.on Events.Click, (event, layer) ->
		switchSelection(this, layer)
ret.states.add
	blur: {blur:20}
	unblur: {blur:0}

scroll = new ScrollComponent
    width: Screen.width
    height: 1220
    scrollHorizontal:false
    borderRadius:8
#scroll.centerX()
scroll.y = 560
scroll.states.add
	blur: {
		blur:20
		scrollVertical:false
	}
	unblur: {
		blur:0
		scrollVertical:true
	}
 
# scroll.on Events.ScrollEnd, (event, layer) ->
# 	selectionDisabled = false
# 	print "Enabling"
for i in [0..stations.length - 1]
	layer = new Layer
		superLayer: scroll.content
		backgroundColor: "#dddddd"
		width:Screen.width - 20
		height: 100
		x:10
		y:(100+20)*i
		html:stations[i]
		style: 
			"font-size":"40px"
			"text-align":"center"	
			"padding":"35px"
			"color":"#111111"
			"font-weight":"bold"

	layer.on Events.Click, (event, layer) ->
		switchSelection(this, layer)

# TODO: Make me do something on click
goButton = new Layer
	x:10
	y:1800
	width:(Screen.width - 20)
	backgroundColor: "#eee"
	html:"Go!"
	style:
		"font-size":"60px"
		"padding":"35px"
		"text-align":"center"
		"font-weight":"bold"
goButton.states.add
	blur: {blur:20}
	unblur: {blur:0}
goButton.on Events.Click, (event, button) ->
	if not selectedLayer is false
		blurUI()

popup = new Layer
	width: Screen.width*2/3
	height: Screen.height/3 + 50
	backgroundColor: "#3c9eff"
	style:
		"text-align":"center"
		"font-weight":"bold"
		"padding":"75px"
		"font-size":"40px"
		"line-height":"100%"
	opacity:0
	y:1800
popup.centerX()
popup.html = "You are going to catch a<br> SFO Airport train<br>at Embarcadero station.<br><br>See your watch for pacing information."

popup.states.add
	visible: {
		opacity:1
		y:Screen.height/2 - Screen.height/6
	}
	hidden: {
		opacity:0
		y:1800
	}

startNav = new Layer
	superLayer: popup
	width: Screen.width/2
	height: 125
	y: 340 
	backgroundColor: "#2be880"
	html:"Enable turn-by-turn directions"
	style:
		"text-align":"center"
		"font-weight":"bold"
		"padding-top":"20px"
startNav.centerX()
startNav.on Events.Click, (event, button) ->
	if selectionDisabled is true
		if navEnabled is true
			button.html = "Enable turn-by-turn directions"
			button.backgroundColor ="#2be880"
			button.style =
				"color":"#ffffff"
			navEnabled = false
		else
			button.html = "Disable turn-by-turn directions"
			button.backgroundColor ="#d3ff30"
			button.style =
				"color":"#000000"
			navEnabled = true

escape = new Layer
	superLayer: popup
	width: Screen.width/2
	height: 125
	y: 485 
	backgroundColor: "#ff3836"
	html:"<div style='font-size:40px'>Abort!</div>Select a different destination"
	style:
		"text-align":"center"
		"font-weight":"bold"
		"padding-top":"20px"
		"font-size":"30px"
escape.centerX()
escape.on Events.Click, (event, button) ->
	unBlurUI()

### Functions ###
blurUI = () ->
	selectionDisabled = true
	help.states.switch("blur")
	home.states.switch("blur")
	ret.states.switch("blur")
	scroll.states.switch("blur")
	goButton.states.switch("blur")
	popup.states.switch("visible")


unBlurUI = () ->
	selectionDisabled = false
	help.states.switch("unblur")
	home.states.switch("unblur")
	ret.states.switch("unblur")
	scroll.states.switch("unblur")
	goButton.states.switch("unblur")
	popup.states.switch("hidden")


switchSelection = (caller, layer) ->
	if selectionDisabled is true
		return
	if not selectedLayer is false
		selectedLayer.backgroundColor = "#dddddd"
	if selectedLayer is caller
		selectedLayer.backgroundColor = "#dddddd"
		goButton.backgroundColor = "#eee"
		selectedLayer = false
	else
		layer.backgroundColor = "#2be880"
		goButton.backgroundColor = "#2be880"
		selectedLayer = layer
