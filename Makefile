compile:
	javac -d bin -cp "lib/*" src/academic/model/*.java src/academic/driver/*.java

run: compile
	java -cp "bin;lib/*" academic.driver.Driver1

clean:
	@if exist bin rmdir /s /q bin && mkdir bin

.PHONY: compile run clean
