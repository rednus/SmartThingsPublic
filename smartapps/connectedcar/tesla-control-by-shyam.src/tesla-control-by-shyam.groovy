definition(
    name: "Tesla-Control (By Shyam)",
    namespace: "connectedcar",
    author: "Shyam Avvari",
    description: "Integrate your Tesla car with SmartThings",
    category: "SmartThings Labs",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/tesla-app%402x.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/tesla-app%403x.png",
    singleInstance: true 
){
	appSetting "CLIENT_ID"
    appSetting "CLIENT_SECRET"
    appSetting "OWNER_API_URI"
}

preferences {
	page(name: "loginToTesla", title: "Login to Tesla")
	page(name: "selectCars", title: "Select Your Tesla")
}

def loginToTesla() {
	log.debug "Showing screen 1"
    log.debug "State : ${state}"
    if(isAccessTokenValid().success == true) {
    	return dynamicPage(name: "loginToTesla", title: "Connect your MyTesla Account", nextPage:"selectCars", uninstall:true) {
        	section("Tesla Login Active, Continue to Car Selection") {}
        }
    } else {
		return dynamicPage(name: "loginToTesla", title: "Connect your MyTesla Account", nextPage:"selectCars", uninstall:true) {
    		section("MyTesla Credentials:") {
        		input "username", "text", title: "Username", required: true, autoCorrect:false
            	input "password", "password", title: "Password", required: true, autoCorrect:false
        	}
    	}
	}
}

def selectCars() {
	//if valid token not present @@ this need refinement
    def loginResult = []
    if(isAccessTokenValid().success == false) {
		log.debug "Tesla Login.."
		loginResult = login()
        
    	log.debug "Login Complete.."
    } else {
    	loginResult = [success:true]
    }
    
	if(loginResult.success){
    	def options = []
        options = carsDiscovered() 
        log.debug "Options : ${options}"
        
        //if no cars present or all cars already added
        if (options.size() == 0){
        	return dynamicPage(name: "selectCars", title: "Nothing to Do", install: true, uninstall:true) { 
            	section("Login Success") {}
                section("All cars already added, or no cars available to add") {}
            }
        } else {
        	return dynamicPage(name: "selectCars", title: "Select Your Tesla", install: true, uninstall:true) { 
        		section("Login Success") {}
            	section("Select which Tesla to connect"){
            		input(name: "selectedCar", type: "enum", required:false, multiple:false, options:options)
            	}
			}
        }
    } else {
    	return dynamicPage(name: "selectCars", title: "Tesla", uninstall:true) {
        	section("Login Failed") {}
        }
    }
}

def installed() {
	log.debug "Installed"
	initialize()
}

def updated() {
	log.debug "Updated"
	//unsubscribe()
	//initialize()
}

def uninstalled() {
	log.debug "Uninstalled"
    if(getChildDevices()){
    	removeChildDevices(getChildDevices())
    }
}

def initialize() {
	if (selectCars) {
		addDevice()
	}
}
//def appHandler(evt){
//	log.debug "app event ${evt.name}:${evt.value} received"
//}
//CHILD DEVICE METHODS
def addDevice() {
	log.debug "Selected Car : ${selectedCar}"
    //double check if the device is already added
    if(!getChildDevice(selectedCar)){ 
    	//find the selected car in the car list
        def car = [:]
        state.cars.each {
        	if (it.vin == selectedCar){
        		car = it
            }
        }
        //add device
    	def d = addChildDevice("connectedcar", "Tesla", "${car.id}", null, [label: "${car.display_name} - ${car.vin}", name: "Tesla"])
        //d.subscribe("switch", appHandler)
        log.debug "Car added to Devices..."
    }
}
private removeChildDevices(delete)
{
	log.debug "Deleting ${delete.size()} Teslas"
	delete.each {
		//state.suppressDelete[it.deviceNetworkId] = true
		deleteChildDevice(it.deviceNetworkId)
		//state.suppressDelete.remove(it.deviceNetworkId)
	}
}
//Car LIST Methods
Map carsDiscovered() {
	def devices = getcarList()
    log.debug "Preparing Options... ${devices}"
    def map = [:]
   	devices.each {
    	//remove a device if already added
        if(!getChildDevice(it?.vin)){
			def value = "${it?.display_name}"
			def key = "${it?.vin}"
			map["${key}"] = value
    	}
	}
    log.debug "Options are ready...${map}"
	return map    
}

def getcarList() {
	def devices = []
    def carListParams = [
    	uri: appSettings.OWNER_API_URI,
        path: "/api/1/vehicles",
        contentType: ANY,
        headers: ["Authorization": "${state.token_type} ${state.access_token}"]
    ]
    log.debug "Getting Car List...${carListParams}"
    try {
    	httpGet(carListParams) { resp ->
    		log.debug resp.status
    		if(resp.status == 200) {
            	log.debug "Preparing Cars List..."
        		resp.data.response.each {
                	if (it?.remote_start_enabled == true){
                		devices += it
                    }
                }
                log.debug "CarList ${devices}"
        	} else {
        		log.error "car list: unknown response"
        	}
    	}
	} catch (groovyx.net.http.HttpResponseException e) {
    		log.debug "Failed Http Call"
			result.reason = "Bad login"
	}
    //store car list in the state
    state.cars = devices
    log.debug "State with Cars: $state"
	return devices
}

//login methods
private forceLogin(){
	log.debug "Removing state values..."
	deleteState()
    login()
}

private login() {
	if(isAccessTokenValid().success == true) {
		return [success:true]
	}
	return doLogin()
}

private doLogin() {
	def grant_type = "password"
	def client_id = appSettings.CLIENT_ID
    def client_secret = appSettings.CLIENT_SECRET
	def loginParams = [
		uri: appSettings.OWNER_API_URI,
        path: "/oauth/token",
		contentType: ANY,
		body: "grant_type=${grant_type}&client_id=${client_id}&client_secret=${client_secret}&email=${username}&password=${password}"
	]
	def result = [success:false]

    try {
    	log.debug "Attempting Login.."
    	httpPost(loginParams) { resp ->
        	if (resp.status == 200) {
            	log.debug "Login Success..."
                log.debug "Setting State values..."
                setState(resp.data.access_token, resp.data.token_type, resp.data.created_at, resp.data.expires_in, resp.data.refresh_token)
                result.success = true 
            } else {
            	log.debug "Bad Response" & resp.status
            	result.reason = "Bad login"
                result.status = resp.status
            }
    	}
	} catch (groovyx.net.http.HttpResponseException e) {
    		log.debug "Failed Http Call"
			result.reason = "Bad login"
	}
	return result
}


//state update routines
private deleteState(){
	state.access_token = ""
    state.token_type = ""
    state.created_at = (long) 0
    state.expires_in = (long) 0
    state.refresh_token = ""
    state.cars = ""
    log.debug "Deleted State: $state"
}

private setState(String access_token, String token_type, long created_at, long expires_in, String refresh_token){
	state.access_token = access_token
    state.token_type = token_type
    state.created_at = created_at
    state.expires_in = expires_in
    state.refresh_token = refresh_token
    log.debug "New State: $state"
}
private setStateCarsList(String[] cars){
	state.cars = cars
    log.debug "New State: $state"
}
private isAccessTokenValid(){
	def result = [success:false]
    if(state.access_token){
		if ((long) state.created_at != 0 ){
    		log.debug "Checking previous token validity..."
			def exp = new Date( (state.created_at + state.expires_in) * 1000)
            log.debug "Token Expires: ${exp}"
    		def now = new Date()
    		if (state.created_at + state.expires_in > ((long) now.getTime() /1000) ){
            	log.debug "Valid Token"
	        	result = [success:true]
    	    } 
    	}
    } 	
    return result
}

//car status
private String refresh(String vehicle_id){
	//def id = getVehicleId(vin)
    return "Poll Success"
}

//car command events
private command(cmd, vehicle_id){ 
	log.debug "Command received ${cmd} For Car ${vehicle_id}"
    //check if the login token valid
    if(isAccessTokenValid().success == false) {
    	log.debug "Token expired.. "
        return
    }
    //prepare header
    def commandpath = ""
    switch(cmd){
    	case "honk":
        	commandpath = "/api/1/vehicles/${vehicle_id}/command/honk_horn"
            break
        case "flash":
        	commandpath = "/api/1/vehicles/${vehicle_id}/command/flash_lights"
            break
        case "lock":
        	commandpath = "/api/1/vehicles/${vehicle_id}/command/door_lock"
            break
        case "unlock":
        	commandpath = "/api/1/vehicles/${vehicle_id}/command/door_unlock"
            break
        default:
        	log.debug "No Command received..."
            return
    }
    log.debug "Command path is ${commandpath}"
    //first find the child
    def car = getChildDevice(vehicle_id)
    if(car){
    	def commParams = [
        	uri: appSettings.OWNER_API_URI,
        	path: commandpath,
        	headers: ["Authorization": "${state.token_type} ${state.access_token}" ],
            body: ""
        ]
        log.debug commParams
    	try{
        	httpPost(commParams) { resp -> 
        		if(resp.status == 200){
            		log.debug "Command ${cmd} Executed.."
            	} else {
            		log.debug "Could not contact/${cmd} the car, something went wrong..."
            	}
    		}
		} catch (groovyx.net.http.HttpResponseException e) {
			log.debug "Could not contact/${cmd} the car, internet error... $e"
		}
	}
}