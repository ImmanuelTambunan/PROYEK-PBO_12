compile:
	@if not exist bin mkdir bin
	javac -d bin -cp "lib/*" src/academic/model/*.java src/academic/service/*.java src/academic/driver/*.java

run: compile
	java -cp "bin;lib/*" academic.driver.Driver1

clean:
	@if exist bin rmdir /s /q bin && mkdir bin

reset: clean
	@if exist akademik.db del akademik.db

.PHONY: compile run clean reset
