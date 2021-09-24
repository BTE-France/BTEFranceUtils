package fr.maxyolo01.btefranceutils.test.discord;

import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.managers.ChannelManager;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.pagination.MessagePaginationAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DummyTextChannel implements TextChannel {

    private String topic = "Dummy topic";
    private boolean nsfw = false;
    private boolean news = false;
    private int slowMode = 0;
    private ChannelType type = ChannelType.TEXT;
    private String name = "dummy-channel";
    private List<Member> members = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private List<MessageEmbed> embeds = new ArrayList<>();

    @Nullable
    @Override
    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean isNSFW() {
        return this.nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    @Override
    public boolean isNews() {
        return this.news;
    }

    public void setNews(boolean news) {
        this.news = news;
    }

    @Override
    public int getSlowmode() {
        return this.slowMode;
    }

    public void setSlowMode(int slowMode) {
        this.slowMode = slowMode;
    }

    @NotNull
    @Override
    public ChannelType getType() {
        return this.type;
    }

    public void setType(@NotNull ChannelType type) {
        this.type = type;
    }

    @Override
    public long getLatestMessageIdLong() {
        return 0;
    }

    @Override
    public boolean hasLatestMessage() {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return null;
    }

    @Nullable
    @Override
    public Category getParent() {
        return null;
    }

    @NotNull
    @Override
    public List<Member> getMembers() {
        return this.members;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public int getPositionRaw() {
        return 0;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @Nullable
    @Override
    public PermissionOverride getPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getPermissionOverrides() {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getMemberPermissionOverrides() {
        return null;
    }

    @NotNull
    @Override
    public List<PermissionOverride> getRolePermissionOverrides() {
        return null;
    }

    @Override
    public boolean isSynced() {
        return false;
    }

    @NotNull
    @Override
    public ChannelAction<TextChannel> createCopy(@NotNull Guild guild) {
        return null;
    }

    @NotNull
    @Override
    public ChannelAction<TextChannel> createCopy() {
        return null;
    }

    @NotNull
    @Override
    public ChannelManager getManager() {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @NotNull
    @Override
    public PermissionOverrideAction createPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public PermissionOverrideAction putPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
        return null;
    }

    @NotNull
    @Override
    public InviteAction createInvite() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Invite>> retrieveInvites() {
        return null;
    }

    @NotNull
    @Override
    public RestAction<List<Webhook>> retrieveWebhooks() {
        return null;
    }

    @NotNull
    @Override
    public WebhookAction createWebhook(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Webhook.WebhookReference> follow(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteMessages(@NotNull Collection<Message> collection) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> deleteMessagesByIds(@NotNull Collection<String> collection) {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deleteWebhookById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s, @NotNull String s1) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(@NotNull String s, @NotNull Emote emote) {
        return null;
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(@NotNull String s, @NotNull String s1, @NotNull User user) {
        return null;
    }

    @Override
    public boolean canTalk() {
        return false;
    }

    @Override
    public boolean canTalk(@NotNull Member member) {
        return false;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public long getIdLong() {
        return 0;
    }

    @Override
    public int compareTo(@NotNull GuildChannel guildChannel) {
        return 0;
    }

    @NotNull
    @Override
    public RestAction<Webhook.WebhookReference> follow(long targetChannelId) {
        return TextChannel.super.follow(targetChannelId);
    }

    @NotNull
    @Override
    public RestAction<Webhook.WebhookReference> follow(@NotNull TextChannel targetChannel) {
        return TextChannel.super.follow(targetChannel);
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(long messageId) {
        return TextChannel.super.clearReactionsById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(long messageId, @NotNull String unicode) {
        return TextChannel.super.clearReactionsById(messageId, unicode);
    }

    @NotNull
    @Override
    public RestAction<Void> clearReactionsById(long messageId, @NotNull Emote emote) {
        return TextChannel.super.clearReactionsById(messageId, emote);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(long messageId, @NotNull String unicode, @NotNull User user) {
        return TextChannel.super.removeReactionById(messageId, unicode, user);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(@NotNull String messageId, @NotNull Emote emote, @NotNull User user) {
        return TextChannel.super.removeReactionById(messageId, emote, user);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(long messageId, @NotNull Emote emote, @NotNull User user) {
        return TextChannel.super.removeReactionById(messageId, emote, user);
    }

    @NotNull
    @Override
    public RestAction<Message> crosspostMessageById(@NotNull String messageId) {
        return TextChannel.super.crosspostMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Message> crosspostMessageById(long messageId) {
        return TextChannel.super.crosspostMessageById(messageId);
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        TextChannel.super.formatTo(formatter, flags, width, precision);
    }

    @NotNull
    @Override
    public PermissionOverrideAction upsertPermissionOverride(@NotNull IPermissionHolder permissionHolder) {
        return TextChannel.super.upsertPermissionOverride(permissionHolder);
    }

    @NotNull
    @Override
    public String getLatestMessageId() {
        return TextChannel.super.getLatestMessageId();
    }

    @NotNull
    @Override
    public synchronized List<CompletableFuture<Void>> purgeMessagesById(@NotNull List<String> messageIds) {
        return TextChannel.super.purgeMessagesById(messageIds);
    }

    @NotNull
    @Override
    public synchronized List<CompletableFuture<Void>> purgeMessagesById(@NotNull String... messageIds) {
        return TextChannel.super.purgeMessagesById(messageIds);
    }

    @NotNull
    @Override
    public synchronized List<CompletableFuture<Void>> purgeMessages(@NotNull Message... messages) {
        return TextChannel.super.purgeMessages(messages);
    }

    @NotNull
    @Override
    public synchronized List<CompletableFuture<Void>> purgeMessages(@NotNull List<? extends Message> messages) {
        return TextChannel.super.purgeMessages(messages);
    }

    @NotNull
    @Override
    public synchronized List<CompletableFuture<Void>> purgeMessagesById(@NotNull long... messageIds) {
        return TextChannel.super.purgeMessagesById(messageIds);
    }

    @NotNull
    @Override
    public synchronized MessageAction sendMessage(@NotNull CharSequence text) {
        return TextChannel.super.sendMessage(text);
    }

    private synchronized List<Message> getMessages() {
        return this.messages;
    }

    @NotNull
    @Override
    public synchronized MessageAction sendMessageFormat(@NotNull String format, @NotNull Object... args) {
        return TextChannel.super.sendMessageFormat(format, args);
    }

    @NotNull
    @Override
    public synchronized MessageAction sendMessage(@NotNull MessageEmbed embed) {
        return new DummyMessageAction( () -> {
            synchronized (this.embeds) {
                this.embeds.add(embed);
                this.embeds.notify();
            }
        });
    }

    public MessageEmbed waitForNextEmbed() throws InterruptedException {
        synchronized (this.embeds) {
            while (this.embeds.size() == 0) {
                this.embeds.wait();
            }
            return this.embeds.remove(0);
        }
    }

    @NotNull
    @Override
    public synchronized MessageAction sendMessage(@NotNull Message msg) {
        return new DummyMessageAction( () -> {
            synchronized (this.embeds) {
                this.messages.add(msg);
                this.messages.notify();
            }
        });
    }

    public synchronized List<MessageEmbed> getEmbeds() {
        return this.embeds;
    }

    @NotNull
    @Override
    public synchronized MessageAction sendFile(@NotNull File file, @NotNull AttachmentOption... options) {
        return TextChannel.super.sendFile(file, options);
    }

    @NotNull
    @Override
    public synchronized MessageAction sendFile(@NotNull File file, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return TextChannel.super.sendFile(file, fileName, options);
    }

    @NotNull
    @Override
    public synchronized MessageAction sendFile(@NotNull InputStream data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return TextChannel.super.sendFile(data, fileName, options);
    }

    @NotNull
    @Override
    public synchronized MessageAction sendFile(@NotNull byte[] data, @NotNull String fileName, @NotNull AttachmentOption... options) {
        return TextChannel.super.sendFile(data, fileName, options);
    }

    @NotNull
    @Override
    public RestAction<Message> retrieveMessageById(@NotNull String messageId) {
        return TextChannel.super.retrieveMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Message> retrieveMessageById(long messageId) {
        return TextChannel.super.retrieveMessageById(messageId);
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deleteMessageById(@NotNull String messageId) {
        return TextChannel.super.deleteMessageById(messageId);
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> deleteMessageById(long messageId) {
        return TextChannel.super.deleteMessageById(messageId);
    }

    @Override
    public MessageHistory getHistory() {
        return TextChannel.super.getHistory();
    }

    @NotNull
    @Override
    public MessagePaginationAction getIterableHistory() {
        return TextChannel.super.getIterableHistory();
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull String messageId, int limit) {
        return TextChannel.super.getHistoryAround(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAround(long messageId, int limit) {
        return TextChannel.super.getHistoryAround(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAround(@NotNull Message message, int limit) {
        return TextChannel.super.getHistoryAround(message, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull String messageId, int limit) {
        return TextChannel.super.getHistoryAfter(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAfter(long messageId, int limit) {
        return TextChannel.super.getHistoryAfter(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryAfter(@NotNull Message message, int limit) {
        return TextChannel.super.getHistoryAfter(message, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull String messageId, int limit) {
        return TextChannel.super.getHistoryBefore(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryBefore(long messageId, int limit) {
        return TextChannel.super.getHistoryBefore(messageId, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryBefore(@NotNull Message message, int limit) {
        return TextChannel.super.getHistoryBefore(message, limit);
    }

    @NotNull
    @Override
    public MessageHistory.MessageRetrieveAction getHistoryFromBeginning(int limit) {
        return TextChannel.super.getHistoryFromBeginning(limit);
    }

    @NotNull
    @Override
    public RestAction<Void> sendTyping() {
        return TextChannel.super.sendTyping();
    }

    @NotNull
    @Override
    public RestAction<Void> addReactionById(@NotNull String messageId, @NotNull String unicode) {
        return TextChannel.super.addReactionById(messageId, unicode);
    }

    @NotNull
    @Override
    public RestAction<Void> addReactionById(long messageId, @NotNull String unicode) {
        return TextChannel.super.addReactionById(messageId, unicode);
    }

    @NotNull
    @Override
    public RestAction<Void> addReactionById(@NotNull String messageId, @NotNull Emote emote) {
        return TextChannel.super.addReactionById(messageId, emote);
    }

    @NotNull
    @Override
    public RestAction<Void> addReactionById(long messageId, @NotNull Emote emote) {
        return TextChannel.super.addReactionById(messageId, emote);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(@NotNull String messageId, @NotNull String unicode) {
        return TextChannel.super.removeReactionById(messageId, unicode);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(long messageId, @NotNull String unicode) {
        return TextChannel.super.removeReactionById(messageId, unicode);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(@NotNull String messageId, @NotNull Emote emote) {
        return TextChannel.super.removeReactionById(messageId, emote);
    }

    @NotNull
    @Override
    public RestAction<Void> removeReactionById(long messageId, @NotNull Emote emote) {
        return TextChannel.super.removeReactionById(messageId, emote);
    }

    @NotNull
    @Override
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull String unicode) {
        return TextChannel.super.retrieveReactionUsersById(messageId, unicode);
    }

    @NotNull
    @Override
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull String unicode) {
        return TextChannel.super.retrieveReactionUsersById(messageId, unicode);
    }

    @NotNull
    @Override
    public ReactionPaginationAction retrieveReactionUsersById(@NotNull String messageId, @NotNull Emote emote) {
        return TextChannel.super.retrieveReactionUsersById(messageId, emote);
    }

    @NotNull
    @Override
    public ReactionPaginationAction retrieveReactionUsersById(long messageId, @NotNull Emote emote) {
        return TextChannel.super.retrieveReactionUsersById(messageId, emote);
    }

    @NotNull
    @Override
    public RestAction<Void> pinMessageById(@NotNull String messageId) {
        return TextChannel.super.pinMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Void> pinMessageById(long messageId) {
        return TextChannel.super.pinMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Void> unpinMessageById(@NotNull String messageId) {
        return TextChannel.super.unpinMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<Void> unpinMessageById(long messageId) {
        return TextChannel.super.unpinMessageById(messageId);
    }

    @NotNull
    @Override
    public RestAction<List<Message>> retrievePinnedMessages() {
        return TextChannel.super.retrievePinnedMessages();
    }

    @NotNull
    @Override
    public MessageAction editMessageById(@NotNull String messageId, @NotNull CharSequence newContent) {
        return TextChannel.super.editMessageById(messageId, newContent);
    }

    @NotNull
    @Override
    public MessageAction editMessageById(long messageId, @NotNull CharSequence newContent) {
        return TextChannel.super.editMessageById(messageId, newContent);
    }

    @NotNull
    @Override
    public MessageAction editMessageById(@NotNull String messageId, @NotNull Message newContent) {
        return TextChannel.super.editMessageById(messageId, newContent);
    }

    @NotNull
    @Override
    public MessageAction editMessageById(long messageId, @NotNull Message newContent) {
        return TextChannel.super.editMessageById(messageId, newContent);
    }

    @NotNull
    @Override
    public MessageAction editMessageFormatById(@NotNull String messageId, @NotNull String format, @NotNull Object... args) {
        return TextChannel.super.editMessageFormatById(messageId, format, args);
    }

    @NotNull
    @Override
    public MessageAction editMessageFormatById(long messageId, @NotNull String format, @NotNull Object... args) {
        return TextChannel.super.editMessageFormatById(messageId, format, args);
    }

    @NotNull
    @Override
    public MessageAction editMessageById(@NotNull String messageId, @NotNull MessageEmbed newEmbed) {
        return TextChannel.super.editMessageById(messageId, newEmbed);
    }

    @NotNull
    @Override
    public MessageAction editMessageById(long messageId, @NotNull MessageEmbed newEmbed) {
        return TextChannel.super.editMessageById(messageId, newEmbed);
    }

    @NotNull
    @Override
    public String getId() {
        return TextChannel.super.getId();
    }

    @NotNull
    @Override
    public OffsetDateTime getTimeCreated() {
        return TextChannel.super.getTimeCreated();
    }
}
