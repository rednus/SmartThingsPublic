/**
 *  Connected Car
 *
 *  Copyright 2016 Shyam Avvari
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Tesla", namespace: "connectedcar", author: "Shyam Avvari") {
        capability "Battery" //using for soc
        capability "Actuator" //using to lock/unlock
		capability "Lock" //using to lock/unlock
        capability "Refresh" //using to get the car status
        //capability "Power"
        //capability "Power Meter"
        //cabin climate
        capability "Thermostat" //using for car climate
        capability "Temperature Measurement"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Heating Setpoint"
        capability "Thermostat Fan Mode"
        capability "Thermostat Mode"
        capability "Thermostat Operating State"
        capability "Thermostat Setpoint"
        capability "Switch" //using this to enable control from alexa
        
        //not working
        attribute "distUnit", "enum", ["km", "mi"]
        attribute "tempUnit", "enum", ["C", "F"]
        attribute "odo", "number" //using for displaying odo meter
        attribute "soclimit", "number" //limit for charging
        attribute "batmiles", "string" //store estimated range for soc
        
       command "honk"
       command "flash"
       command "startcharge"
       command "stopcharge"
       command "chargestandard"
       command "chargemax"
       
       command "climateOn"
       command "climateOff"
       command "tempUp"
       command "tempDown"
       command "chgUp"
       command "chgDown"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		// TODO: define your main and details tiles here
        multiAttributeTile(name:"carstatus", type: "generic", width: 6, height: 4, canChangeIcon: true){
        	tileAttribute ("device.lock", key: "PRIMARY_CONTROL") {
        		attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#79b821", nextState:"unlocked"
				attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff", nextState:"locked"
            }
            tileAttribute ("odo", key: "SECONDARY_CONTROL") {
            	attributeState "default", label: 'ODO: ${currentValue}'
            }
        }
        standardTile("honk", "device.honk", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'Honk', action:"honk", icon:"st.Electronics.electronics12"
        }
        standardTile("flash", "device.flash", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'Flash', action:"flash", icon:"st.Lighting.light11"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("climateonoff", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "off", action:"climateOn", nextState: "auto", icon: "st.thermostat.heating-cooling-off"
            state "heat", action:"climateOff",  nextState: "off", icon: "st.thermostat.heat"
			state "cool", action:"climateOff",  nextState: "off", icon: "st.thermostat.cool"
			state "auto", action:"climateOff",  nextState: "off", icon: "st.thermostat.auto"
		}
        multiAttributeTile(name:"climate", type:"thermostat", width:6, height:4) {
        	tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
            	attributeState("default", label: '${currentValue}', unit:"dC")
            }
            tileAttribute("device.thermostatSetpoint", key: "SECONDARY_CONTROL") {
        		attributeState("default", label:'${currentValue}', unit:"dC")
    		}
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
            	attributeState("idle", backgroundColor:"#919191")
        		attributeState("heating", backgroundColor:"#ffa81e")
        		attributeState("cooling", backgroundColor:"#269bd2")
    		}
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
        		attributeState("off", label:'Off')
        		attributeState("heat", label:'${name}')
        		attributeState("cool", label:'${name}')
        		attributeState("auto", label:'${name}')
    		}
            tileAttribute("device.temperature", key: "VALUE_CONTROL") {
            	attributeState("thermostatSetpoint", label:'${currentValue}', unit:"dC")
        		attributeState("VALUE_UP", action: "tempUp")
        		attributeState("VALUE_DOWN", action: "tempDown")
    		}
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
        		attributeState("default", label:'${currentValue}', unit:"dF")
    		}
    		tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
        		attributeState("default", label:'${currentValue}', unit:"dF")
    		}
        }
        multiAttributeTile(name:"charge", type:"generic", width:6, height:4) {
        	tileAttribute("device.battery", key: "PRIMARY_CONTROL") {
            	attributeState("default", label: '${currentValue}%')
            }
            tileAttribute("device.soclimit", key: "VALUE_CONTROL") {
            	attributeState("soclimit", label: '${currentValue}')
                attributeState("VALUE_UP", action: "chgUp")
        		attributeState("VALUE_DOWN", action: "chgDown")
            }
            tileAttribute("device.batmiles", key: "SECONDARY_CONTROL") {
            	attributeState("batmiles", label: 'Range: ${currentValue}')
            }
        }
		main "carstatus"
        details(["carstatus", "climate", "charge", "refresh", "honk", "flash", "climateonoff"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "X - Parsing '${description}'"
}

// handle commands
def refresh() {
	parent.refresh(device.deviceNetworkId)
}
def lock() {
	parent.command("lock", device.deviceNetworkId)
}
def unlock() {
	parent.command("unlock", device.deviceNetworkId)
}
def honk() {
	parent.command("honk", device.deviceNetworkId)
}
def flash(){
	parent.command("flash", device.deviceNetworkId)
}
def startcharge(){
	parent.command("startcharge", device.deviceNetworkId)
}
def stopcharge(){
	parent.command("stopcharge", device.deviceNetworkId)
}
def openport(){
	parent.command("openport", device.deviceNetworkId)
}
def chargestandard(){
	parent.command("chargestandard", device.deviceNetworkId)
}
def chargemax(){
	parent.command("chargemax", device.deviceNetworkId)
}
def chgUp(){
	parent.setChgLevel(1, device.deviceNetworkId)
}
def chgDown(){
	parent.setChgLevel(-1, device.deviceNetworkId)
}
//thermostat functions
//def on(){
//	log.debug "Switching on climate from alexa"
//	climateOn()
//    }
def climateOn(){
	parent.command("starthvac", device.deviceNetworkId)
}
//def off(){
//	log.debug "Switching on climate from alexa"
//	climateOff()
//}
def climateOff(){
	parent.command("stophvac", device.deviceNetworkId)
}
def setHeatingSetpoint(temp){
	parent.setTemp(temp,device.deviceNetworkId)
}
def setCoolingSetpoint(temp){
	parent.setTemp(temp,device.deviceNetworkId)
}
def tempUp(){
    log.debug "Increasing Temp"
    def cur = device.currentState("thermostatSetpoint")?.value.toFloat()
    log.debug "Current value : ${cur}"
    cur += 0.5
    log.debug "Seeting value to : ${cur}"
	parent.setTemp(cur, device.deviceNetworkId)
}
def tempDown(){
    log.debug "Decreasing Temp"
    def cur = device.currentState("thermostatSetpoint")?.value.toFloat()
    log.debug "Current value : ${cur}"
    cur -= 0.5
    log.debug "Seeting value to : ${cur}"
	parent.setTemp(cur, device.deviceNetworkId)
}