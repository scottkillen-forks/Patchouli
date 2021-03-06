package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonCategory extends Button {

	private static final int ANIM_TIME = 5;

	final GuiBook parent;
	BookCategory category;
	final BookIcon icon;
	final ITextComponent name;
	final int u, v;
	float timeHovered;

	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category, Button.IPressable onPress) {
		this(parent, x, y, category.getIcon(), category.getName(), onPress);
		this.category = category;
	}

	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, ITextComponent name, Button.IPressable onPress) {
		super(parent.bookLeft + x, parent.bookTop + y, 20, 20, name, onPress);
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}

	@Override
	public void renderButton(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		if (active) {
			if (isHovered()) {
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			} else {
				timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			}

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHovered() ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if (locked) {
				RenderSystem.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(ms, parent.book, x + 2, y + 2);
			} else {
				icon.render(ms, x + 2, y + 2);
			}

			ms.push();
			RenderSystem.enableBlend();
			RenderSystem.color4f(1F, 1F, 1F, transparency);
			ms.translate(0, 0, 200);
			GuiBook.drawFromTexture(ms, parent.book, x, y, u, v, width, height);
			RenderSystem.color4f(1F, 1F, 1F, 1F);

			if (category != null && !category.isLocked()) {
				GuiBook.drawMarking(ms, parent.book, x, y, 0, category.getReadState());
			}
			ms.pop();

			if (isHovered()) {
				parent.setTooltip(locked
						? new TranslationTextComponent("patchouli.gui.lexicon.locked").mergeStyle(TextFormatting.GRAY)
						: name);
			}
		}
	}

	@Override
	public void playDownSound(SoundHandler soundHandlerIn) {
		if (category != null && !category.isLocked()) {
			GuiBook.playBookFlipSound(parent.book);
		}
	}

	public BookCategory getCategory() {
		return category;
	}

}
