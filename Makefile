compile:
	@if exist bin rmdir /s /q bin
	@mkdir bin
	@dir /s /b src\*.java > sources.txt
	javac -d bin -cp "lib\*" @sources.txt
	@del sources.txt
 
run: compile
	java -cp "bin;lib\*" academic.driver.Driver1
 
clean:
	@if exist bin rmdir /s /q bin
	@mkdir bin
 
.PHONY: compile run clean