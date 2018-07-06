# winstanley: An IntelliJ Plug-in for WDL

This plug-in currently supports:
* Syntax highlighting
* Collapsible code blocks for workflows, tasks, and more.
* Highlighting and completion of parentheses `()` and other braces.
* Allows auto-commenting of lines in WDL with <kbd>CMD</KBD>+<kbd>/</KBD>
* Undeclared value detection
* "Go to declaration"

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
2. Import this repo into IntelliJ as an existing project ("Import Project" on the initial screen).
    * Accept all of the defaults in the import wizard
    * When prompted to overwrite or reuse the module definition file `winstanley.iml`, you must select "Reuse"
3. Make sure the repo is using the IntelliJ plugin Java SDK
    * Project Structure -> Project -> Project SDK
4. Make sure the repo has a valid Scala SDK attached as a project dependency
    * (Project Structure -> Libraries -> "+" -> Scala SDK)
    * Otherwise you'll see errors like `"Cannot find class WdlElementType"` even though it's clearly there!
5. Generate the necessary files (on Mac):
    * Navigate to Wdl.flex and generate sources using <kbd>CMD</KBD>+<kbd>SHIFT</KBD>+<kbd>G</KBD>. 
      - You may be prompted to choose a destination for the generated sources. If `/$PROJECT_ROOT/gen` doesn't exist, create
      it. Choose `/$PROJECT_ROOT/gen` as the destination.
    * Navigate to wdl.bnf and generate sources using <kbd>CMD</KBD>+<kbd>SHIFT</KBD>+<kbd>G</KBD>
    * It may be necessary to right click on the `gen` folder and select `Mark Directory As>Generated Sources Root`
    * That keyboard shortcut may be stolen by “Find Previous”, nuke it in Preferences -> Keymap and generation will start working
6. At this point, you can run or test the project using IntelliJ's preset run modes. 
    * Open the `Run Configurations` window (for me, in the top right of the IntelliJ window) 
    * Add a new one configuration with the `+` icon.
    * Choose the '`Plugin`' type. The defaults should be good.
      - Note that IntelliJ may hide `Plugin` in the `XX items more (irrelevant)...` section at the end. 
    * When this new `Run Configuration` is run, a new IntelliJ window opens with the plugin preinstalled ready for testing.
    * Additional instructions for setting this up are in the IntelliJ links above.
    * If you see  the message "No plugin module specified for configuration", locate the dropdown "Use classpath of module" and select "winstanley"
      - If there are no entries in the dropdown, this may be because in Project Structure, the `winstanley` module is of type Java (folder with blue square) instead of IntelliJ Platform Plugin (power plug). You can correct this by removing the Java module and replacing it with a new IPP module.
    * If you kill the new IntelliJ window with the "stop" button, you will see the first launch screen every time; quit the application instead.

## Errata

The plug-in was created based on the instructions here: http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html 
