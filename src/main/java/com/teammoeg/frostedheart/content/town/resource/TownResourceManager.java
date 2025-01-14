package com.teammoeg.frostedheart.content.town.resource;

import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * 提供了对城镇资源进行操作的一些方法。
 */
public class TownResourceManager {
    public final TownResourceHolder resourceHolder;

    public TownResourceManager(TownResourceHolder holder){
        this.resourceHolder = holder;
    }

    public TownResourceManager(){
        this.resourceHolder = new TownResourceHolder();
    }

    /**
     * 获取城镇中某个ITownResourceKey的储量。
     * 若为ItemResourceKey，获取的是城镇中，所有该ItemResourceKey对应物品的(物品数量 * 单个物品能转化为ItemResourceKey数量)之和。
     * 这也是TownResourceManager中，两种cost(ItemResourceKey)能消耗的最大数量。
     * 举个例子，比如城镇中，有20个铁锭、10个铁块，1个铁锭可转化为1 metal_1，1个铁块可转化为9 metal_1，使用这个get方法获取metal_1的数量，会得到20*1+10*9=110.
     */
    public double get(ITownResourceKey key){
        return resourceHolder.get(key);
    }

    /**
     * 获取此种ITownResourceType对应的所有资源数量。
     * @return 该ITownResourceType对应的所有ITownResourceKey的资源数量之和。
     */
    public double get(ITownResourceType type){
        return getAllAboveLevel(type,0);
    }

    /**
     * 获取城镇中某个物品的储量。
     */
    public double get(ItemStack itemStack){
        return resourceHolder.get(itemStack);
    }

    /**
     * 获取城镇剩余(未被占用)的容量。
     */
    public double getCapacityLeft(){
        return get(VirtualResourceType.MAX_CAPACITY.generateKey(0)) - resourceHolder.getOccupiedCapacity();
    }

    /**
     * 获取给定的level以上的，某ITownResourceType的资源数量之和。
     */
    public double getAllAboveLevel(ITownResourceType type, int minLevel){
        if(!type.isLevelValid(minLevel)) return 0;
        double sum = 0;
        for(int i=minLevel;i<=type.getMaxLevel();i++){
            sum += get(type.generateKey(i));
        }
        return sum;
    }

    /**
     * 获取给定的level之间的，某ITownResourceType的资源数量之和。
     */
    public double getAllBetweenLevel(ITownResourceType type, int minLevel, int maxLevel){
        if(!type.isLevelValid(minLevel) || !type.isLevelValid(maxLevel)) return 0;
        double sum = 0;
        for(int i=minLevel;i<=maxLevel;i++){
            sum += get(type.generateKey(i));
        }
        return sum;
    }

    /**
     * Add resource to the town if there is enough capacity.
     * @param key The resource key
     * @param amount The amount to add
     */
    public ResourceActionResult addIfHaveCapacity(VirtualResourceKey key, double amount){
        if(!key.type.needCapacity){
            resourceHolder.addUnsafe(key,amount);
            return new ResourceActionResult(true, amount, key);
        }
        double spaceLeft = getCapacityLeft();
        if(spaceLeft>=amount){
            resourceHolder.addUnsafe(key,amount);
            return new ResourceActionResult(true, amount, key);
        }
        return ResourceActionResult.NOT_SUCCESS;
    }

    /**
     * Add resource to the town if there is enough capacity.
     * What actually added is the VirtualResourceKey with level 0.
     * @param type The virtual resource type, which will be converted to a VirtualResourceKey with level 0.
     * @param amount The amount to add
     */
    public ResourceActionResult addIfHaveCapacity(VirtualResourceType type, double amount){
        return addIfHaveCapacity(type.generateKey(0), amount);
    }

    /**
     * Add item to the town if there is enough capacity.
     * @param itemStack The item to add. The count of item will be ignored.
     * @param amount The amount to add
     */
    public ResourceActionResult addIfHaveCapacity(ItemStack itemStack, double amount){
        double spaceLeft = getCapacityLeft();
        if(spaceLeft>=amount){
            resourceHolder.addUnsafe(itemStack, amount);
            return new ResourceActionResult(true, amount, 0, 0);
        }
        return ResourceActionResult.NOT_SUCCESS;
    }

    //You can't add ItemResourceKey to town, because item resource are saved as ItemStack.

    /**
     * Add resource to the town.
     * If space is not enough, some(same amount with capacity left) of the resource will be added, and others will lose.
     * @param amount The amount to add.
     * @return The result of the action. You can know if all the resource is added, and how many resources are added.
     */
    public ResourceActionResult addToMax(VirtualResourceKey key, double amount){
        if(!key.type.needCapacity){
            resourceHolder.addUnsafe(key,amount);
            return new ResourceActionResult(true, amount, key);
        }
        double capacityLeft = getCapacityLeft();
        if(capacityLeft <= 0) return ResourceActionResult.NOT_SUCCESS;
        if(capacityLeft>=amount){
            resourceHolder.addUnsafe(key,amount);
            return new ResourceActionResult(true, amount, key);
        } else {
            resourceHolder.addUnsafe(key,capacityLeft);
            return new ResourceActionResult(false, capacityLeft, key);
        }
    }

    /**
     * Add item to the town.
     * If space is not enough, some(same amount with capacity left) of the resource will be added, and others will lose.
     * @param amount The amount to add.
     * @return The result of the action. You can know if all the resource is added, and how many resources are added.
     */
    public ResourceActionResult addToMax(ItemStack itemStack, double amount){
        double capacityLeft = getCapacityLeft();
        if(capacityLeft <= 0) return ResourceActionResult.NOT_SUCCESS;
        if(capacityLeft>=amount){
            resourceHolder.addUnsafe( itemStack,amount);
            return new ResourceActionResult(true, amount, 0, 0);
        } else {
            resourceHolder.addUnsafe(itemStack,capacityLeft);
            return new ResourceActionResult(false, capacityLeft, 0, 0);
        }
    }

    /**
     * Cost resource from the town.
     * If there is not enough resource, nothing will be cost.
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costIfHaveEnough(VirtualResourceKey key, double amount){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        if(get(key) >= amount){
            resourceHolder.costUnsafe(key,amount);
            return new ResourceActionResult(true, amount, key);
        } else {
            return ResourceActionResult.NOT_SUCCESS;
        }
    }

    /**
     * Cost item from the town.
     * If there is not enough item, nothing will be cost.
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costIfHaveEnough(ItemStack itemStack, double amount){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        if(get(itemStack) >= amount){
            resourceHolder.costUnsafe(itemStack,amount);
            return new ResourceActionResult(true, amount, 0, 0);
        } else {
            return ResourceActionResult.NOT_SUCCESS;
        }
    }

    /**
     * Cost resource from the town.
     * If there is not enough resource, nothing will be cost.
     * ItemResourceKey对应的所有物品都有可能被消耗。
     * 消耗的模式是：按照某种顺序，先消耗某种物品直到消耗完，再消耗下一种物品。
     * 我也不知道这个“某种顺序”究竟是什么，这取决于从缓存中读取的顺序。
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costIfHaveEnough(ItemResourceKey key, double amount){
        double resourceLeft = get(key);
        if(resourceLeft<=amount) return ResourceActionResult.NOT_SUCCESS;
        double toCost;
        Map<ItemStack, Double> items = resourceHolder.getAllItems(key);
        toCost = amount;
        for(ItemStack itemStack : items.keySet()){
            double itemResourceAmount = TownResourceHolder.getResourceAmount(itemStack, key);
            ResourceActionResult result = costToEmpty(itemStack, toCost / itemResourceAmount);
            toCost -= result.actualAmount() * itemResourceAmount;
            if(toCost<=TownResourceHolder.DELTA) break;
        }
        return new ResourceActionResult(true, amount, key);
    }

    public ResourceActionResult costIfHaveEnough(ITownResourceKey key, double amount){
        if(key instanceof ItemResourceKey) return costIfHaveEnough((ItemResourceKey)key, amount);
        if(key instanceof VirtualResourceKey) return costIfHaveEnough((VirtualResourceKey)key, amount);
        return ResourceActionResult.NOT_SUCCESS;
    }

    /**
     * Cost item from the town.
     * If there is not enough resource, all resource left will be cost.
     * @param itemStack The item to cost. The count of item will be ignored.
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costToEmpty(ItemStack itemStack, double amount){
        double resourceLeft = get(itemStack);
        if(resourceLeft<=0) return ResourceActionResult.NOT_SUCCESS;
        if(resourceLeft>=amount){
            resourceHolder.costUnsafe(itemStack,amount);
            return new ResourceActionResult(true, amount);
        } else {
            resourceHolder.costUnsafe(itemStack,resourceLeft);
            return new ResourceActionResult(false, resourceLeft);
        }
    }

    /**
     * Cost resource from the town.
     * If there is not enough resource, all resource left will be cost.
     * ItemResourceKey对应的所有物品都有可能被消耗。
     * 消耗的模式是：按照某种顺序，先消耗某种物品直到消耗完，再消耗下一种物品。
     * 我也不知道这个“某种顺序”究竟是什么，这取决于从缓存中读取的顺序。
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costToEmpty(ItemResourceKey key, double amount){
        double resourceLeft = get(key);
        double toCost;
        Map<ItemStack, Double> items = resourceHolder.getAllItems(key);
        toCost = Math.min(resourceLeft, amount);
        for(ItemStack itemStack : items.keySet()){
            double itemResourceAmount = TownResourceHolder.getResourceAmount(itemStack, key);
            ResourceActionResult result = costToEmpty(itemStack, toCost / itemResourceAmount);
            toCost -= result.actualAmount() * itemResourceAmount;
            if(toCost<=TownResourceHolder.DELTA) break;
        }
        return new ResourceActionResult(amount <= resourceLeft, Math.min(resourceLeft, amount), key);
    }

    /**
     * Cost resource from the town.
     * If there is not enough resource, nothing will be cost.
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costToEmpty(VirtualResourceKey key, double amount){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        double resourceLeft = get(key);
        if(resourceLeft<=0) return ResourceActionResult.NOT_SUCCESS;
        resourceHolder.costUnsafe(key,Math.min(resourceLeft, amount));
        return new ResourceActionResult(true, Math.min(resourceLeft, amount), key);
    }

    /**
     * Cost resource from the town.
     * If there is not enough resource, nothing will be cost.
     * @return The result of the action. You can know if all the resource is costed, and how many resources are costed, etc.
     */
    public ResourceActionResult costToEmpty(ITownResourceKey key, double amount){
        if(key instanceof ItemResourceKey) return costToEmpty((ItemResourceKey)key, amount);
        else if (key instanceof VirtualResourceKey) return costToEmpty((VirtualResourceKey)key, amount);
        return ResourceActionResult.NOT_SUCCESS;
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType、且等级在给定level之间的的资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, nothing will be cost.
     */
    public ResourceActionResult costBetweenLevelIfHaveEnough(ITownResourceType type, double amount, int minLevel, int maxLevel){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        if(minLevel>maxLevel) return ResourceActionResult.NOT_SUCCESS;
        if(!type.isLevelValid(minLevel) || !type.isLevelValid(maxLevel)) return ResourceActionResult.NOT_SUCCESS;
        double resourceLeft = getAllBetweenLevel(type,minLevel, maxLevel);
        if(resourceLeft < amount) {
            return ResourceActionResult.NOT_SUCCESS;
        }
        double resourcesToCost = amount;
        int minLevelCount = maxLevel;
        double averageLevelCount = 0;
        for(int level = minLevel; level <= maxLevel; level++){
            if(resourcesToCost<=0) break;
            ResourceActionResult result = costToEmpty(type.generateKey(level), resourcesToCost);
            resourcesToCost -= result.actualAmount();
            if(result.allSuccess()){
                minLevelCount = Math.min(minLevelCount, level);
            }
            averageLevelCount += result.actualAmount() * level;
        }
        averageLevelCount /= amount;
        return new ResourceActionResult(true, amount, minLevelCount, averageLevelCount);
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType、且等级大于minLevel的资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, nothing will be cost.
     */
    public ResourceActionResult costAboveLevelIfHaveEnough(ITownResourceType type, double amount, int minLevel){
        return costBetweenLevelIfHaveEnough(type, amount, minLevel, type.getMaxLevel());
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType的资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, nothing will be cost.
     */
    public ResourceActionResult costLowestLevelIfHaveEnough(ITownResourceType type, double amount){
        return costAboveLevelIfHaveEnough(type, amount, 0);
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType的资源都可以被消耗。
     * 消耗顺序为：先消耗level高的TownResourceKey，后消耗level低的
     * If there is not enough resource, nothing will be cost.
     */
    public ResourceActionResult costHighestLevelIfHaveEnough(ITownResourceType type, double amount){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        int maxLevel = type.getMaxLevel();
        double resourceLeft = getAllBetweenLevel(type,0, maxLevel);
        if(resourceLeft < amount) {
            return ResourceActionResult.NOT_SUCCESS;
        }
        double resourcesToCost = amount;
        int minLevelCount = maxLevel;
        double averageLevelCount = 0;
        for(int level = maxLevel; level >= 0; level--){
            if(resourcesToCost<=0) break;
            ResourceActionResult result = costToEmpty(type.generateKey(level), resourcesToCost);
            resourcesToCost -= result.actualAmount();
            if(result.allSuccess()){
                minLevelCount = Math.min(minLevelCount, level);
            }
            averageLevelCount += result.actualAmount() * level;
        }
        averageLevelCount /= amount;
        return new ResourceActionResult(true, amount, minLevelCount, averageLevelCount);
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType，且等级在给定level之间的资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, all resource left will be cost.
     */
    public ResourceActionResult costBetweenLevelToEmpty(ITownResourceType type, double amount, int minLevel, int maxLevel){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        if(minLevel>maxLevel) return ResourceActionResult.NOT_SUCCESS;
        if(!type.isLevelValid(minLevel) || !type.isLevelValid(maxLevel)) return ResourceActionResult.NOT_SUCCESS;
        double resourceLeft = getAllBetweenLevel(type,minLevel, maxLevel);
        double resourcesToCost = Math.min(amount, resourceLeft);
        int minLevelCount = maxLevel;
        double averageLevelCount = 0;
        for(int level = minLevel; level <= maxLevel; level++){
            if(resourcesToCost<=0) break;
            ResourceActionResult result = costToEmpty(type.generateKey(level), resourcesToCost);
            resourcesToCost -= result.actualAmount();
            if(result.allSuccess()){
                minLevelCount = Math.min(minLevelCount, level);
            }
            averageLevelCount += result.actualAmount() * level;
        }
        averageLevelCount /= amount;
        return new ResourceActionResult(true, Math.min(amount, resourceLeft), minLevelCount, averageLevelCount);
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType，且等级大于等于minLevel的资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, all resource left will be cost.
     */
    public ResourceActionResult costAboveLevelToEmpty(ITownResourceType type, double amount, int minLevel){
        return costBetweenLevelToEmpty(type, amount, minLevel, type.getMaxLevel());
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType资源都可以被消耗。
     * 消耗顺序为：先消耗level低的TownResourceKey，后消耗level高的
     * If there is not enough resource, all resource left will be cost.
     */
    public ResourceActionResult costLowestLevelToEmpty(ITownResourceType type, double amount){
        return costBetweenLevelToEmpty(type, amount, 0, 0);
    }

    /**
     * Cost resource from the town.
     * 所有此TownResourceType，且等级在给定level之间的资源都可以被消耗。
     * 消耗顺序为：先消耗level高的TownResourceKey，后消耗level低的
     * If there is not enough resource, all resource left will be cost.
     */
    public ResourceActionResult costHighestLevelToEmpty(ITownResourceType type, double amount){
        if(amount <=0) return ResourceActionResult.NOT_SUCCESS;
        int maxLevel = type.getMaxLevel();
        double resourceLeft = getAllBetweenLevel(type,0, maxLevel);
        double resourcesToCost = Math.min(amount, resourceLeft);
        int minLevelCount = maxLevel;
        double averageLevelCount = 0;
        for(int level = maxLevel; level >= 0; level--){
            if(resourcesToCost<=0) break;
            ResourceActionResult result = costToEmpty(type.generateKey(level), resourcesToCost);
            resourcesToCost -= result.actualAmount();
            if(result.allSuccess()){
                minLevelCount = Math.min(minLevelCount, level);
            }
            averageLevelCount += result.actualAmount() * level;
        }
        averageLevelCount /= amount;
        return new ResourceActionResult(true, Math.min(amount, resourceLeft), minLevelCount, averageLevelCount);
    }

    /**
     * Set the amount of resource.
     * 对于需要容量的资源，不建议使用这个方法，因为这可能使资源储量超过上限。
     */
    @Deprecated
    public void set(VirtualResourceKey key, double amount){
        resourceHolder.set(key, amount);
    }

    @Deprecated
    public void set(VirtualResourceType type, double amount){
        resourceHolder.set(type.generateKey(0), amount);
    }

    /**
     * 将所有服务资源设置为0
     */
    public void resetAllServices(){
        Arrays.stream(VirtualResourceType.values())
                .filter(type -> type.isService)
                .forEach(type -> set(type,0));
    }
}
