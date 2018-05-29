package virassan.entities.creatures.utils;

import java.awt.Graphics;
import java.awt.Rectangle;

import virassan.entities.creatures.Creature;
import virassan.gfx.Animation;
import virassan.gfx.hud.SkillText;
import virassan.main.Handler;

public class SkillTracker {

	private BuffTracker buff;
	private Skill skillType;
	private Creature entity;
	private Class target;
	private Animation skillAnimation;
	int x, y, skillCount;
	private long skillTimer, skillLast;
	
	public SkillTracker(Creature entity, Skill skill, Animation anime){
		this.entity = entity;
		this.target = skill.getTarget();
		skillType = skill;
		skillAnimation = anime;
		if(skill.getType().equals("buff")){
			buff = new BuffTracker(null, skill.getBuff());
		}
		skillCount = skillType.getCooldown();
	}
	
	public void tick(double delta){
		skillTimer += (System.currentTimeMillis() - skillLast);
		skillLast = System.currentTimeMillis();
		if(skillTimer >= 1000){
			skillCount++;
			skillTimer = 0;
		}
	}
	
	public void action(Creature targ, Creature from){
		if(skillCount >= skillType.getCooldown()){
			double distance = Math.sqrt(Math.pow(from.getX() - targ.getX() + targ.getWidth(), 2) + Math.pow(from.getY() - targ.getY()+ targ.getHeight(), 2));
			if(distance <= getRange() || getRange() == 0){
				switch(skillType.getType()){
				case "buff": buff.setTarget(targ); break; // The buff is created when the skill is so no need to call any methods
				case "dmg": from.attack(this, targ, skillType.getEffectType()); break;
				case "summon": break;
				case "restore": from.restore(this, targ); break;
				}
				skillCount = 0;
			}else{
				Handler.GAMESTATE.getHUD().addSkillList(new SkillText(entity.getHandler(), "Out of Range", Handler.LAVENDER, (int)targ.getX()-10, (int)targ.getY()-10));
			}
		}else{
			Handler.GAMESTATE.getHUD().addSkillList(new SkillText(entity.getHandler(), "Skill Not Ready Yet", Handler.LAVENDER, (int)from.getX() - 20, (int)from.getY()-10));
		}
	}
	
	public void drawIcon(Graphics g, int x, int y){
		g.drawImage(skillType.getIcon(), x, y, null);
		this.x = x;
		this.y = y;
		if(skillCount < skillType.getCooldown()){
			g.setColor(Handler.SELECTION_LOWLIGHT);
			g.fillRect(x, y, skillType.getIcon().getWidth(), skillType.getIcon().getHeight());
		}
	}
	
	// GETTERS AND SETTERS
	public int getRange(){
		return skillType.getRange();
	}
	
	public BuffTracker getBuff(){
		return buff;
	}
	
	public void setBuff(BuffTracker buff){
		this.buff = buff;
	}
	
	public void setSkillCount(int skillCount){
		this.skillCount = skillCount;
	}
	
	public void setTarget(Class t){
		target = t;
	}
	
	public Class getTarget(){
		return target;
	}
	
	public void setEntity(Creature c){
		entity = c;
	}
	
	public Creature getEntity(){
		return entity;
	}
	
	public void setSkillAnimation(Animation anime){
		skillAnimation = anime;
	}
	
	public Animation getSkillAnimation(){
		return skillAnimation;
	}
	
	public String toString(){
		return skillType.getName();
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 64, 64);
	}
	
	public Skill getSkillType(){
		return skillType;
	}

	public String getSkillCostType() {
		return skillType.getCostType();
	}
	
	public float getCost(){
		return skillType.getCost();
	}
}
