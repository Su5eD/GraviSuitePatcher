# Gravitation Suite Patcher

Fixes several issues in Gravitation Suite

## Fixed Issues

- The Advanced Drill [crashes](https://pastebin.com/HjFY2MHH) the game when used in a Miner.  
  Cause: Missing required override of methods `ItemDrill#energyUse`, `ItemDrill#breakTime` and `ItemDrill#breakBlock`.  
  Fix: Added overrides for the mentioned methods.
- Ocassional [crash](https://pastebin.com/LSz9Hcx1) when using the Advanced Drill  
  Cause: A null `RayTrace` is passed into `ItemAdvancedDrill#getBrokenBlocks`, while it's expected to be non-null.  
  Fix: In case of the ray trace being null, return an empty `Collection`.
- The Ultimate Lappack has a recipe with a missing item showing up in JEI.  
  Cause: The Lappack was used in the recipe, which is hidden in the Experimental profile.  
  Fix: Added a condition to only register the recipe when using the Classic profile.
