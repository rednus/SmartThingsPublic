/**
 *  Connected Car - Charging virtual device
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
	definition (name: "Tesla-Charging", namespace: "connectedcar", author: "Shyam Avvari") {
        capability "Battery" //using for soc
        capability "Switch"
        capability "Switch Level"
        //capability "Power"
        //capability "Power Meter"
        
        //not working
        attribute "distUnit", "enum", ["km", "mi"]
        attribute "tempUnit", "enum", ["C", "F"]
        
       command "startcharge"
       command "stopcharge"
       command "chargestandard"
       command "chargemax"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    	standardTile("charge", "device.battery", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'Charge-Dummy'
        }
        main "charge"
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "X - Parsing '${description}'"
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
def on(){
	log.debug "Starting Charging from alexa"
	startcharge()
    }
def off(){
	log.debug "Stopping charging from alexa"
	stopcharge()
}
def setLevel(lvl){
	log.debug "Setting charge level to ${lvl}"
    sendEvent(name: "level", value: lvl, unit: "%")
    if(lvl == 100)
    	chargemax()
    //else if(lvl == 90)
    //	chargestandard()
    else
    	parent.command("chargelevel", device.deviceNetworkId)
}