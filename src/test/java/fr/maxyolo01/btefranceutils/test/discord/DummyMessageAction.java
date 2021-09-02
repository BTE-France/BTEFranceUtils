package fr.maxyolo01.btefranceutils.test.discord;

import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.IMentionable;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.RateLimitedException;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class DummyMessageAction implements MessageAction {

    private Runnable onQueue;

    DummyMessageAction(Runnable onQueue) {
        this.onQueue = onQueue;
    }
    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public MessageAction setCheck(@Nullable BooleanSupplier booleanSupplier) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction timeout(long l, @NotNull TimeUnit timeUnit) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction deadline(long l) {
        return null;
    }

    @Override
    public void queue(@Nullable Consumer<? super Message> consumer, @Nullable Consumer<? super Throwable> consumer1) {
        this.onQueue.run();
    }

    @Override
    public Message complete(boolean b) throws RateLimitedException {
        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<Message> submit(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MessageChannel getChannel() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEdit() {
        return false;
    }

    @NotNull
    @Override
    public MessageAction apply(@Nullable Message message) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction referenceById(long l) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction mentionRepliedUser(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction failOnInvalidReply(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction tts(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction reset() {
        return null;
    }

    @NotNull
    @Override
    public MessageAction nonce(@Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction content(@Nullable String s) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction embed(@Nullable MessageEmbed messageEmbed) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction append(@Nullable CharSequence charSequence, int i, int i1) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction append(char c) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction addFile(@NotNull InputStream inputStream, @NotNull String s, @NotNull AttachmentOption... attachmentOptions) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction addFile(@NotNull File file, @NotNull String s, @NotNull AttachmentOption... attachmentOptions) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction clearFiles() {
        return null;
    }

    @NotNull
    @Override
    public MessageAction clearFiles(@NotNull BiConsumer<String, InputStream> biConsumer) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction clearFiles(@NotNull Consumer<InputStream> consumer) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction override(boolean b) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction allowedMentions(@Nullable Collection<Message.MentionType> collection) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction mention(@NotNull IMentionable... iMentionables) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction mentionUsers(@NotNull String... strings) {
        return null;
    }

    @NotNull
    @Override
    public MessageAction mentionRoles(@NotNull String... strings) {
        return null;
    }
}
