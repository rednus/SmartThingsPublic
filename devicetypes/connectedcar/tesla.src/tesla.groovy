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
		capability "Alarm"
        capability "Battery"
		capability "Light"
		capability "Lock"
		capability "Polling"
        capability "Refresh"
        capability "Switch"
		capability "Temperature Measurement" 
        capability "Power"
        
       command "honk"
       command "flash"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		// TODO: define your main and details tiles here
        multiAttributeTile(name:"carstatus", type: "car", width: 6, height: 4, canChangeIcon: true){
        	tileAttribute ("device.lock", key: "PRIMARY_CONTROL") {
        		attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#79b821", nextState:"unlocked"
				attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#ffffff", nextState:"locked"
            }
            tileAttribute ("power", key: "SECONDARY_CONTROL") {
            	attributeState "mains", label:'', icon:"st.Transportation.transportation6"
                attributeState "battery", label:'', icon:"st.Transportation.transportation8"
            }
        }
        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false) {
			state "battery", label:'${currentValue}% SoC', unit:"kWh"
		}
        standardTile("honk", "device.honk", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'', action:"honk", icon:"st.Electronics.electronics12"
        }
        standardTile("flash", "device.flash", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'', action:"flash", icon:"st.Lighting.light11"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        main "carstatus"
        details(["carstatus", "battery", "refresh", "honk", "flash"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'battery' attribute
	// TODO: handle 'switch' attribute
	// TODO: handle 'lock' attribute
	// TODO: handle 'temperature' attribute
	// TODO: handle 'Honk' attribute

}

// handle commands
def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def on() {
	log.debug "Executing 'on'"
	honk()
}

def lock() {
	parent.command("lock", device.deviceNetworkId)
}

def unlock() {
	parent.command("unlock", device.deviceNetworkId)
}

def poll() {
	log.debug "Executing 'poll' for ${device.deviceNetworkId}"
    //results = parent.poll(device.deviceNetworkId)
    log.debug "Result: $result"
	// TODO: handle 'poll' command
}

def refresh() {
	parent.refresh(device.deviceNetworkId)
}

def honk() {
	parent.command("honk", device.deviceNetworkId)
}

def flash(){
	parent.command("flash", device.deviceNetworkId)
}

def siren() {
	honk()
}

def strobe() {
	flash()
}

def both(){
	honk()
    flash()
}