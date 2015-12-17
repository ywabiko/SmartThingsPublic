/**
 *  Bath Fan By Humidity
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
    name: "Bath Fan By Humidity",
           namespace: "ywabiko",
           author: "Yasuhiro Wabiko",
           description: "Turn on bath fan while humidity is above a threshold.",
           category: "My Apps",
           iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
           iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
           iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Humidity Sensors") {
        input "thehumids", "capability.relativeHumidityMeasurement", required: true, title:"Where?", multiple:true
        input "threshold",  "number", title: "Humidity?"
    }
    section("Fan Switches") {
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
    thehumids.each {
        subscribe(it, "humidity", humidityHandler)
    }
    log.debug "initialize: threshold is ${threshold}"
}

def humidityHandler(evt) {
    log.debug "humidityHandler: humidity = ${evt.value}"

    if (Double.parseDouble(evt.value.replace("%", "")) > threshold) {
        log.debug "humidityHandler: turning on fans."
        theswitches.each { it.on() }
    } else if (Double.parseDouble(evt.value.replace("%", "")) <= threshold ) {
        log.debug "humidityHandler: turning off fans."
        theswitches.each { it.off() }
    }
}
