/**
 *  Pretend Presence
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
import groovy.time.TimeCategory
definition(
    name: "Pretend Presence",
           namespace: "ywabiko",
           author: "Yasuhiro Wabiko",
           description: "Turn on lights randomly to pretend presence during vacation.",
           category: "My Apps",
           iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
           iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
           iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Light Switches") {
        input "theswitches", "capability.switch", required: true, title:"Where?", multiple:true
        input "starttime", "time", required:true, title:"Start Time?"
        input "endtime",   "time", required:true, title:"End Time?"
        input "variation_start", "number", required:true, title:"Variation for Start (in min)?"
        input "variation_end", "number", required:true, title:"Variation for End (in min)?"
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
    update_schedule();
    def runin = next_starttime - now();
    log.debug "initialize: scheduling first turnOn: ${runin}"
    runIn(runin, turnOnHandler)
}

def update_schedule() {
    Random random = new Random()
    state.offset_start = random.nextInt(variation_start * 2) - variation_start
    state.offset_end   = random.nextInt(variation_end * 2)   - variation_end

	use (TimeCategory) {
		state.next_starttime = starttime + state.offset_start.minutes
    	state.next_endtime   = endtime   + state.offset_end.minutes
	}
    log.debug "update_schedule: ${offset_start} ${offset_end} ${next_startime} ${next_endtime}"
}

def turnOnHandler(evt) {
    def runin = state.next_endtime - now();
    log.debug "turnOnHandler: scheduling next turnOff: ${runin}"
    runIn(runin, turnOffHandler)
}

def turnOffHandler(evt) {
    theswitches.each { it.off() }

    update_schedule();
    def runin = state.next_starttime - now();
    log.debug "turnOffHandler: scheduling next turnOn: ${runin}"
    runIn(runin, turnOnHandler)
}


