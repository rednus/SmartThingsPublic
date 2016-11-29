/**
 *  Connected Car - Climate virtual device
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
	definition (name: "Tesla-ClimateOnOff", namespace: "connectedcar", author: "Shyam Avvari") {
        capability "Switch"
        
        attribute "climate", "enum", ["on", "off"]
	}


	simulator {
	}

	tiles {
    	standardTile("clima", "device.climate", inactiveLabel: false, decoration: "flat"){
        	state "default", label:'Climate'
        }
        main "clima"
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "X - Parsing '${description}'"
}

def on(){
	log.debug "Starting Charging from alexa"
	parent.command("starthvac", device.deviceNetworkId)
    }
def off(){
	log.debug "Stopping charging from alexa"
	parent.command("stophvac", device.deviceNetworkId)
}