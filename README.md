# winstanley: An IntelliJ Plug-in for WDL

This plug-in currently supports:
* Syntax highlighting
* Collapsible code blocks for workflows, tasks, and more.
* Highlighting and completion of parentheses `()` and other braces.
* Allows auto-commenting of lines in WDL with <kbd>CMD</KBD>+<kbd>/</KBD>
* Undeclared value detection
* "Go to declaration" (currently for declared values only)

More features will be coming soon!

## Getting Started

### As a Plug-in User

The plugin is now part of the IntelliJ Plugin Repository. To install:
* Open the IntelliJ Preferences page
* Click on "Plugins"
* Click on "Browse Repositories "
* Search for and install "Winstanley WDL"

### Or, as a Developer
To build or test the plugin using IntelliJ:

1. Get set up with the pre-requisites listed here: http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html
2. Create a new IntelliJ project using this repo as the existing source.
3. Make sure the repo is using the IntelliJ plugin Java SDK 
4. Make sure the repo has a valid Scala SDK attached as a project dependency.
  * Otherwise you'll see errors like `"Cannot find class WdlElementType"` even though it's clearly there!
5. Generate the necessary files (on Mac):
  * Navigate to Wdl.flex and generate sources using <kbd>CMD</KBD>+<kbd>SHIFT</KBD>+<kbd>G</KBD>. 
  * Navigate to wdl.bnf and generate sources using <kbd>CMD</KBD>+<kbd>SHIFT</KBD>+<kbd>G</KBD>
6. At this point, you can run or test the project using IntelliJ's preset run modes. 
  * Open the `Run Configurations` window (for me, in the top right of the IntelliJ window) 
  * Add a new one configuration with the `+` icon.
  * Choose the '`Plugin`' type. The defaults should be good.
  * When this new `Run Configuration` is run, a new IntelliJ window opens with the plugin preinstalled ready for testing.
  * Additional instructions for setting this up are in the IntelliJ links above.

## Errata

The plug-in was created based on the instructions here: http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html 
