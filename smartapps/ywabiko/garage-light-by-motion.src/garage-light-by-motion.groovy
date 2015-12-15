/**
 *  Garage Light By Motion
 *
 *  Copyright 2015 Yasuhiro Wabiko
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
definition(
    name: "Garage Light By Motion",
    namespace: "ywabiko",
    author: "Yasuhiro Wabiko",
    description: "Turn on light when any of motion sensors reports motion.\r\nTurn off light when none of motion sensors report motion.\r\n",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


preferences {
	section("Motion Sensors") {
		input "themotions", "capability.motionSensor", required: true, title:"Where?", multiple:true
    }
    section("Light Switches") {
        input "theswitches", "capability.switch", required: true, title:"Where?", multiple:true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	for (themotion in themotions) {
		subscribe(themotion, "motion.active", motionDetectedHandler)
		subscribe(themotion, "motion.inactive", motionStoppedHandler)
	}
}

def motionDetectedHandler(evt) {
	if (themotions.any { it.value == "active" })
    {
		for (theswitch in theswitches)
		{
        	theswitch.on();
		}
	}
}
def motionStoppedHandler(evt) {
	if (themotions.any { it.value == "active" })
    {
		for (theswitch in theswitches)
		{
        	theswitch.on();
		}
	}
}
