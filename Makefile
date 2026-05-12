compile:
	@if exist bin rmdir /s /q bin
	@mkdir bin
	powershell -NoProfile -Command "javac -d bin -cp 'lib/*' (Get-ChildItem -Recurse -Filter *.java src).FullName"

run: compile
	java -cp "bin;lib/*" academic.driver.Driver1

clean:
	@if exist bin rmdir /s /q bin && mkdir bin

.PHONY: compile run clean
