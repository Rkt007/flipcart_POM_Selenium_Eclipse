# flipcartTest

This is a Selenium TestNG Java project using Page Object Model for testing Flipkart.

Quick start (Windows - cmd.exe):

1. Prerequisites
   - Java JDK 11+ installed and JAVA_HOME set.
   - Apache Maven installed and added to PATH. Verify with:

     mvn -v

2. Run tests
   - From project root (where `pom.xml` is located):

     cd /d C:\Users\HP\OneDrive\Desktop\FlipcartPOM\flipcartTest
     mvn -DskipTests=false test

   - To run a single test class (example):

     mvn -Dtest=TestClass test

3. Troubleshooting
   - "'mvn' is not recognized" — install Maven and add it to PATH.
   - Browser driver issues — this project uses WebDriverManager; ensure internet access or pre-install drivers.
   - Flaky tests due to timing — consider increasing waits in `BaseClass` or adding explicit waits in page objects.

4. Logs & reports
   - TestNG reports and screenshots (if any) are under `test-output/` and `Logs/`.

If you want, I can:
- Add a small smoke test that only verifies the homepage loads.
- Parameterize timeouts or add a helper wait utility.
