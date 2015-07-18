An Eclipse plugin to support execution of Liquibase scripts against Eclipse Data Sources.

## Installation ##
The plugin can be installed from the Eclipse Marketplace [![](https://marketplace.eclipse.org/sites/all/modules/custom/marketplace/images/installbutton.png)](http://marketplace.eclipse.org/content/liquibase-eclipse-plugin) or directly from the [update site](https://liquibase-eclipse-plugin.googlecode.com/svn/trunk/com.svcdelivery.liquibase.eclipse.site/)

To install a beta release you can download the Liquibase Eclipse plugin jar and the liquibase-osgi jar from the downloads page and place them in your Eclipse plugins folder.

## Usage ##

Right click on a Liquibase XML file to access the Liquibase menu items for updating and rolling back scripts.

Projects containing Liquibase scripts should be converted to Liquibase projects (Right click->configure->Convert to Liquibase Project)

The following views are also available:

  * Liquibase Data Sources - View DTP Data Sources and drill into them to see and rollback installed change sets.
  * Liquibase Results - View the results of executing a Liquibase script.
  * Liquibase Scripts - A quick view of Liquibase scripts available in your workspace (projects must be tagged as Liquibase projects first)