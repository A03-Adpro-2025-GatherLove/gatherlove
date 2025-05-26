@echo off
echo Running JMeter Profile Tests...

REM Check for JMeter bat or jar
if defined JMETER_HOME (
    echo Using JMeter from environment variable: %JMETER_HOME%
    set JMETER_PATH=%JMETER_HOME%
) else (
    echo JMETER_HOME environment variable is not set.
    echo Please enter the full path to your JMeter directory or jar file
    echo Example directory: C:\apache-jmeter-5.6.3
    echo Example JAR: C:\apache-jmeter-5.6.3\bin\ApacheJMeter.jar
    set /p JMETER_PATH="Enter JMeter path: "
)

REM Create directories for results if they don't exist
if not exist "test-results" mkdir test-results
if not exist "test-results\profile" mkdir test-results\profile

echo Starting JMeter test execution...

REM Check if path is to jar file
if "%JMETER_PATH:~-4%"==".jar" (
    echo Running JMeter directly using JAR file
    java -jar "%JMETER_PATH%" -n -t src\test\jmeter\profile-test-plan.jmx -l test-results\profile\results.jtl -e -o test-results\profile\report
) else (
    REM Try to find jmeter.bat
    if exist "%JMETER_PATH%\bin\jmeter.bat" (
        echo Using jmeter.bat from %JMETER_PATH%\bin
        call "%JMETER_PATH%\bin\jmeter.bat" -n -t src\test\jmeter\profile-test-plan.jmx -l test-results\profile\results.jtl -e -o test-results\profile\report
    ) else (
        echo Could not find jmeter.bat at %JMETER_PATH%\bin
        echo Trying to run with JAR file...

        if exist "%JMETER_PATH%\bin\ApacheJMeter.jar" (
            echo Found ApacheJMeter.jar, running with Java
            java -jar "%JMETER_PATH%\bin\ApacheJMeter.jar" -n -t src\test\jmeter\profile-test-plan.jmx -l test-results\profile\results.jtl -e -o test-results\profile\report
        ) else (
            echo ERROR: Could not find JMeter executable or JAR file.
            echo Please check your JMeter installation.
            pause
            exit /b 1
        )
    )
)

if %ERRORLEVEL% EQU 0 (
    echo Test completed successfully.
    echo Results saved to test-results\profile\results.jtl
    echo HTML report generated in test-results\profile\report folder.
) else (
    echo Test execution failed with error code %ERRORLEVEL%.
    echo Please check if JMeter is installed correctly and the test plan exists.
)

pause
