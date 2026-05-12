compile:
	@if not exist bin mkdir bin
	powershell -NoProfile -Command "$$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $$_.FullName }; javac -d bin -cp 'lib/*' $$files"

run: compile
	java -cp "bin;lib/*" academic.driver.Driver1

clean:
	@if exist bin rmdir /s /q bin && mkdir bin

.PHONY: compile run clean
