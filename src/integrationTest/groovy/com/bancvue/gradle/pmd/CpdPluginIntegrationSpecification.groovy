/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.pmd
import com.bancvue.exception.ExceptionSupport
import org.gradle.tooling.BuildException

@Mixin(ExceptionSupport)
class CpdPluginIntegrationSpecification extends AbstractCpdPluginIntegrationSpecification {

	private int minTokenCount = 10

	def setup() {
		buildFile << """
apply plugin: 'java'
apply plugin: 'cpd'

repositories {
	mavenCentral()
}

cpd {
    minimumTokenCount ${minTokenCount}
}
		"""
	}

	def "should detect cpd violation and write xml and html report"() {
		given:
		classFileWithDuplicateTokens("src/main/java/Class.java", minTokenCount)

		when:
		run("check")

		then:
		thrown(BuildException)
		file("build/reports/cpd/all.xml").text =~ /duplication/
		file("build/reports/cpd/all.html").exists()
	}

	def "should enable sourceSet-specific tasks if createUnifiedReport set to false"() {
		emptyClassFile("src/main/java/bv/SomeClass.java")
		classFileWithDuplicateTokens("src/mainTest/java/bv/MainTestClass.java", minTokenCount)
		buildFile << """
apply plugin: 'test-ext'

cpd {
	createUnifiedReport false
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		file("build/reports/cpd/mainTest.xml").text =~ /duplication/
		file("build/reports/cpd/mainTest.html").exists()
	}

	def "should not create unified report task if createUnifiedReport set to false"() {
		buildFile << """
cpd {
	createUnifiedReport false
}
"""

		when:
		run(CpdPlugin.UNIFIED_REPORT_TASK_NAME)

		then:
		BuildException ex = thrown()
		getRootCause(ex).message =~ /Task '${CpdPlugin.UNIFIED_REPORT_TASK_NAME}' not found in root project/
	}

	def "should fail if duplicate token threshold exceeded in files across sourceSets"() {
		given:
		int halfMinTokenCount = (minTokenCount / 2) as int
		classFileWithDuplicateTokens("src/main/java/bv/SomeClass.java", halfMinTokenCount)
		buildFile << """
apply plugin: 'test-ext'
"""

		when:
		run("check")

		then:
		notThrown(Exception)

		when:
		classFileWithDuplicateTokens("src/mainTest/java/bv/MainTestClass.java", halfMinTokenCount)
		run("check")

		then:
		thrown(BuildException)
		file("build/reports/cpd/all.xml").text =~ /duplication/
	}

	def "should work with pmd versions < 5"() {
		given:
		classFileWithDuplicateTokens("src/main/java/Class.java", minTokenCount)
		buildFile << """
cpd {
	toolVersion = "4.3"
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		file("build/reports/cpd/all.xml").text =~ /duplication/
	}

}