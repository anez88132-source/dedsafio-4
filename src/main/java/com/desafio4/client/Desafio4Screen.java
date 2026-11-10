package com.desafio4.client;

import com.desafio4.data.Defs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class Desafio4Screen extends Screen {
    private int tab = 0;
    private double scroll = 0;
    private int contentHeight = 0;
    private int px, py, pw, ph;
    private ItemStack hovered = ItemStack.EMPTY;

    public Desafio4Screen() {
        super(Component.translatable("screen.desafio4.title"));
    }

    @Override
    protected void init() {
        pw = Math.min(340, width - 20);
        ph = Math.min(height - 50, 260);
        px = (width - pw) / 2;
        py = (height - ph) / 2 + 12;
        int bw = (pw - 8) / 3;
        String[] names = {"tab.desafio4.quests", "tab.desafio4.objects", "tab.desafio4.dimensions"};
        for (int i = 0; i < 3; i++) {
            final int t = i;
            addRenderableWidget(Button.builder(Component.translatable(names[i]), b -> {
                tab = t;
                scroll = 0;
            }).bounds(px + i * (bw + 4), py - 22, bw, 20).build());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int maxScroll() {
        return Math.max(0, contentHeight - (ph - 8));
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        scroll = Mth.clamp(scroll - delta * 14, 0, maxScroll());
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.drawCenteredString(font, ClientData.defs.title, width / 2, py - 36, 0xFFFFFF);
        g.fill(px, py, px + pw, py + ph, 0xCC000000);
        g.fill(px, py, px + pw, py + 1, 0xFF888888);
        g.fill(px, py + ph - 1, px + pw, py + ph, 0xFF888888);
        g.fill(px, py, px + 1, py + ph, 0xFF888888);
        g.fill(px + pw - 1, py, px + pw, py + ph, 0xFF888888);

        hovered = ItemStack.EMPTY;
        scroll = Mth.clamp(scroll, 0, maxScroll());
        int x1 = px + 4, y1 = py + 4, x2 = px + pw - 4, y2 = py + ph - 4;
        g.enableScissor(x1, y1, x2, y2);
        int start = y1 - (int) scroll;
        int end;
        if (tab == 0) end = drawQuests(g, x1, start, x2 - x1, mx, my);
        else if (tab == 1) end = drawObjects(g, x1, start, x2 - x1, mx, my);
        else end = drawDimensions(g, x1, start, x2 - x1);
        g.disableScissor();
        contentHeight = end - start;

        super.render(g, mx, my, pt);
        if (!hovered.isEmpty()) g.renderTooltip(font, hovered, mx, my);
    }

    private boolean inPanel(int mx, int my) {
        return mx > px && mx < px + pw && my > py + 3 && my < py + ph - 3;
    }

    private int drawQuests(GuiGraphics g, int x, int y, int w, int mx, int my) {
        Defs d = ClientData.defs;
        if (d.quests.isEmpty()) {
            g.drawString(font, Component.translatable("gui.desafio4.empty"), x + 2, y + 2, 0x888888);
            return y + 14;
        }
        for (Defs.Quest q : d.quests) {
            boolean done = ClientData.isDone(q.id);
            int max = Math.max(1, q.amount);
            int cur = done ? max : Math.min(max, ClientData.progress(q.id));

            g.drawString(font, (done ? "✔ " : "") + q.title, x + 2, y, done ? 0x55FF55 : 0xFFFF55);
            y += 11;
            Component obj = Component.translatable("type.desafio4." + q.type.toLowerCase())
                    .append(": ").append(max + "x ").append(ClientData.targetName(q));
            g.drawString(font, obj, x + 2, y, 0x55FFFF);
            y += 11;
            for (FormattedCharSequence l : font.split(Component.literal(q.description), w - 4)) {
                g.drawString(font, l, x + 2, y, 0xAAAAAA, false);
                y += 10;
            }
            int bw = w - 4;
            g.fill(x + 2, y, x + 2 + bw, y + 10, 0xFF333333);
            g.fill(x + 2, y, x + 2 + bw * cur / max, y + 10, done ? 0xFF2E9E2E : 0xFFC9A227);
            g.drawCenteredString(font, cur + "/" + max, x + 2 + bw / 2, y + 1, 0xFFFFFF);
            y += 14;

            if (!q.rewards.isEmpty()) {
                Component lbl = Component.translatable("gui.desafio4.reward");
                g.drawString(font, lbl, x + 2, y + 4, 0xFFAA00);
                int ix = x + 6 + font.width(lbl);
                for (Defs.Reward r : q.rewards) {
                    Item it = ClientData.item(r.item);
                    if (it == Items.AIR) continue;
                    ItemStack st = new ItemStack(it, Math.max(1, r.count));
                    g.renderItem(st, ix, y);
                    g.renderItemDecorations(font, st, ix, y);
                    if (inPanel(mx, my) && mx >= ix && mx < ix + 16 && my >= y && my < y + 16) hovered = st;
                    ix += 18;
                }
                y += 18;
            }
            y += 6;
            g.fill(x, y - 3, x + w, y - 2, 0x44FFFFFF);
        }
        return y;
    }

    private int drawObjects(GuiGraphics g, int x, int y, int w, int mx, int my) {
        Map<String, String> objs = ClientData.objects();
        if (objs.isEmpty()) {
            g.drawString(font, Component.translatable("gui.desafio4.empty"), x + 2, y + 2, 0x888888);
            return y + 14;
        }
        for (Map.Entry<String, String> e : objs.entrySet()) {
            Item it = ClientData.item(e.getKey());
            if (it == Items.AIR) continue;
            ItemStack st = new ItemStack(it);
            g.renderItem(st, x + 2, y);
            if (inPanel(mx, my) && mx >= x + 2 && mx < x + 18 && my >= y && my < y + 16) hovered = st;
            g.drawString(font, st.getHoverName(), x + 24, y + 1, 0xFFFFFF);
            int ly = y + 12;
            for (FormattedCharSequence l : font.split(Component.literal(e.getValue()), w - 26)) {
                g.drawString(font, l, x + 24, ly, 0xAAAAAA, false);
                ly += 10;
            }
            y = Math.max(y + 20, ly) + 4;
        }
        return y;
    }

    private int drawDimensions(GuiGraphics g, int x, int y, int w) {
        Defs d = ClientData.defs;
        if (d.dimensions.isEmpty()) {
            g.drawString(font, Component.translatable("gui.desafio4.empty"), x + 2, y + 2, 0x888888);
            return y + 14;
        }
        for (Defs.Dim dim : d.dimensions) {
            boolean v = ClientData.visited(dim.id);
            String name = dim.name.isEmpty() ? dim.id : dim.name;
            g.drawString(font, name, x + 2, y, v ? 0x55FF55 : 0xFFFFFF);
            Component st = Component.translatable(v ? "gui.desafio4.visited" : "gui.desafio4.not_visited");
            g.drawString(font, st, x + w - 4 - font.width(st), y, v ? 0x55FF55 : 0xFF5555);
            y += 11;
            g.drawString(font, dim.id, x + 2, y, 0x666666, false);
            y += 10;
            for (FormattedCharSequence l : font.split(Component.literal(dim.description), w - 4)) {
                g.drawString(font, l, x + 2, y, 0xAAAAAA, false);
                y += 10;
            }
            y += 6;
            g.fill(x, y - 3, x + w, y - 2, 0x44FFFFFF);
        }
        return y;
    }
}
