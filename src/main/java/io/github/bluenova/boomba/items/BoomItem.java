package io.github.bluenova.boomba.items;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.inventory.ItemStack;

public class BoomItem {

     private final String id;
     private final String name;
     private final String description;

     protected boolean active = false;

     public BoomItem(String id, String name, String description) {
          this.id = id;
          this.name = name;
          this.description = description;
     }

     public String getId() {
          return id;
     }

     public String getName() {
          return name;
     }

     public String getDescription() {
          return description;
     }

     public ItemStack createTemplateItem() {
          // This method should be overridden by subclasses to create the actual ItemStack with the correct metadata.
          Boomba.getInstance().getLogger().warning("createTemplateItem() not implemented for item: " + id);
          return null;
     }

     // Items are passive by default; subclasses can override if needed.
     public void enable() {
          this.active = true;
     }

     public void disable() {
          this.active = false;
     }

     public boolean isActive() {
          return active;
     }

     @Override
     public int hashCode() {
          return id == null ? 0 : id.hashCode();
     }
}
