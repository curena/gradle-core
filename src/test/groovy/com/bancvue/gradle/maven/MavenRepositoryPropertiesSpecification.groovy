/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.maven

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class MavenRepositoryPropertiesSpecification extends Specification {

	private Project project
	private MavenRepositoryProperties properties

	void setup() {
		project = ProjectBuilder.builder().build()
		properties = new MavenRepositoryProperties(project)
	}

	def "hasCredentialsDefined should return false if username only defined"() {
		when:
		properties.username = "username"

		then:
		!properties.hasCredentialsDefined()
	}

	def "hasCredentialsDefined should return false if password only defined"() {
		when:
		properties.password = "password"

		then:
		!properties.hasCredentialsDefined()
	}

	def "hasCredentialsDefined should return false if username and password not defined"() {
		expect:
		!properties.hasCredentialsDefined()
	}
	
	def "hasCredentialsDefined should return true if username and password defined"() {
		when:
		properties.username = "username"
		properties.password = "password"

		then:
		properties.hasCredentialsDefined()
	}
}
