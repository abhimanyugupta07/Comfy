# Comfy
Comfy is a Java library to read configuration files in INI format and returns a `ComfyConfig` object.

# Building Comfy

	mvn clean package
	
It will generate the package `comfy-0.0.1-SNAPSHOT.jar` in `target` folder.

# Using Comfy

Once published on maven central, Comfy can be added as a maven dependency in your Java project.

## Usage

Comfy takes in two arguments, a path to the config file and an array of `Overrides` to load from the config. If no override is provided, then the lines with overrides are skipped.

Please note that if duplicate override keys are provided, Comfy will return only the last loaded key.

```
ComfyConfig config = Comfy.loadConfig("/path/to/config/file", new String[] { "override_1", "override_2" });
ComfySection section = config.get("section_name");
ConfigValue configValue = config.get("section_name").get("property_key");
```

Helper methods are provided on `ComfySection` object for converting to appropriate types as follows:

```
Integer intValue = config.get("section_name").getIntegerValue("int_key");
String stringValue = config.get("section_name").getStringValue("string_key");
Boolean booleanValue = config.get("section_name").getBooleanValue("boolean_key");
String[] arrayValue = config.get("section_name").getArrayValue("array_key");
```

These get methods throw `ClassCastException` if an invalid conversion is attempted. eg. when trying to retrieve a `BooleanValue` as `IntValue`.

## Running Comfy

To test the functionality of Comfy, Comfy Jar comes with a main class and can be run as follows:

	java -jar target/comfy-0.0.1-SNAPSHOT.jar /Users/abhgupta/Desktop/workspace/comfy/config.ini
	
### Sample Output

```
config.get("common").getIntValue("paid_users_size_limit") = 2147483647
config.get("ftp").getStringValue("name") = hello there, ftp uploading
config.get("ftp").getStringValue("lastname") = null
config.get("http").getArrayValue("params") = [Ljava.lang.String;@16c0663d
config.get("ftp").getBooleanValue("enabled") = false
config.get("ftp").getStringValue("path") = /etc/var/uploads
config.get("ftp") = com.abhimanyu.comfy.config.ComfySection@23223dd8
```

# Test Coverage
*95.9%*
	
# Important features of Design

* Main class: `com.abhimanyu.comfy.Comfy`.
* Line parsing logic: `FilteringLineParser` implements interface `LineParser`.
* Value processing logic: `INIPropertyProcessor` implements interface `PropertyProcessor`.
* Integration tests: `ComfyTest`.

# Assumptions

* A setting key cannot contain delimiter `<`, `>` or new line character.
* Boolean strings supported = {"yes", "no", "true", "false"}.
* Integer values upto `Integer.MAX_VALUE` are supported as Integer value. This can be extended to support `long` as well.
