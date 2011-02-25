
Spring contexts are grouped to facilitate wildcard inclusion, such as classpath*:/spring/common/*-context.xml

Variations for runtime support for different runtime platforms to support different runtime scenarios can then be in other folders under /spring/ 

Folders defined so far:
- spring/common - contexts required for testing and runtime
- spring/runtime - context required only for generic runtime use