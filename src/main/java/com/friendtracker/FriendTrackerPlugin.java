package com.friendtracker;

import com.friendtracker.io.SaveManager;
import com.friendtracker.ui.FriendTrackerPanel;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(
	name = "Friend Tracker"
)
public class FriendTrackerPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private FriendTrackerConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	private SaveManager saveManager;

	private FriendTrackerPanel panel;
	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		saveManager = new SaveManager(client);
		panel = new FriendTrackerPanel(client, this, config, saveManager, new OkHttpClient());

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Friend Tracker")
				.icon(icon)
				.priority(9)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	public void refreshList()
	{
		net.runelite.api.Friend[] friendList = this.client.getFriendContainer().getMembers();

		panel.reset();

		for(net.runelite.api.Friend friend:friendList)
		{
			panel.lookup(friend.getName());
		}

		panel.rebuild();
	}

	@Provides
	FriendTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FriendTrackerConfig.class);
	}
}
