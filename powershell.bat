@echo off
REM Local proxy for powershell - this script will call existing powershell or pwsh executable on the system.
REM It is placed in project root so running `mvnw.cmd` will find it if `powershell` isn't on PATH.



























EXIT /B 1
necho Error: No PowerShell executable found. Please install Windows PowerShell or PowerShell Core and add it to PATH.)  EXIT /B %ERRORLEVEL%  "%ProgramFiles(x86)%\PowerShell\7\pwsh.exe" %*IF EXIST "%ProgramFiles(x86)%\PowerShell\7\pwsh.exe" ()  EXIT /B %ERRORLEVEL%  "%ProgramFiles%\PowerShell\7\pwsh.exe" %*IF EXIST "%ProgramFiles%\PowerShell\7\pwsh.exe" ()  EXIT /B %ERRORLEVEL%  "%SystemRoot%\system32\WindowsPowerShell\v1.0\powershell.exe" %*
nREM Try well-known install locations
nIF EXIST "%SystemRoot%\system32\WindowsPowerShell\v1.0\powershell.exe" ()  EXIT /B %ERRORLEVEL%  pwsh %*IF %ERRORLEVEL%==0 (where pwsh >nul 2>&1
nREM Try PowerShell Core 'pwsh' in PATH)  EXIT /B %ERRORLEVEL%  powershell %*IF %ERRORLEVEL%==0 (where powershell >nul 2>&1nREM Try system 'powershell' in PATH