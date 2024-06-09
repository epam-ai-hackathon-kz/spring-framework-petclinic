/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.VetSchedule;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2002-2022 the original author or authors.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Controller class for handling vet-related requests
 * Authors: Juergen Hoeller, Mark Fisher, Ken Krebs, Arjen Poutsma
 */
@Controller
public class VetController {

    private final ClinicService clinicService;

    @Autowired
    public VetController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("/vets")
    public String showVetList(Map<String, Object> model) {
        Vets vets = getVets();
        model.put("vets", vets);
        return "vets/vetList";
    }

    @GetMapping(value = "/vets.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Vets showJsonVetList() {
        return getVets();
    }

    @GetMapping(value = "/vets.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Vets showXmlVetList() {
        return getVets();
    }

    @GetMapping(value = "/vets/pets/visits")
    public String initNewVisitForm(@RequestParam("date") String date, Map<String, Object> model) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @GetMapping("/vets/schedule")
    public String showVetSchedule(Map<String, Object> model) {
        List<VetSchedule> vetSchedules = clinicService.findAllVetSchedules();
        model.put("vetSchedules", vetSchedules);
        return "vets/vetSchedule";
    }

    private Vets getVets() {
        Vets vets = new Vets();
        vets.getVetList().addAll(this.clinicService.findVets());
        return vets;
    }
}
