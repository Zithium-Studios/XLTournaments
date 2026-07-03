package net.zithium.tournaments.discord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebhookQueue {

    private final Plugin plugin;
    private final ConcurrentLinkedQueue<QueuedWebhook> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean processing = new AtomicBoolean(false);

    // Discord allows ~5 requests per 2s per webhook; 500ms spacing keeps us safely under that.
    private static final long DEFAULT_DELAY_MS = 500L;

    public WebhookQueue(Plugin plugin) {
        this.plugin = plugin;
    }

    private record QueuedWebhook(DiscordWebhook webhook, String tournamentId) {}

    public void enqueue(DiscordWebhook webhook, String tournamentId) {
        queue.add(new QueuedWebhook(webhook, tournamentId));
        if (processing.compareAndSet(false, true)) {
            processNext(0L);
        }
    }

    private void processNext(long delayTicks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::process, delayTicks);
    }

    private void process() {
        QueuedWebhook queued = queue.poll();
        if (queued == null) {
            processing.set(false);
            return;
        }

        long nextDelay = ticksFromMillis(DEFAULT_DELAY_MS);

        try {
            queued.webhook().execute();
        } catch (MalformedURLException ex) {
            plugin.getLogger().severe(
                    "Unable to send Discord webhook for tournament '" + queued.tournamentId() + "': Invalid URL");
        } catch (IOException ex) {
            long retryAfterMs = extractRetryAfterMs(ex);
            if (retryAfterMs > 0) {
                plugin.getLogger().warning(
                        "Webhook rate limited for tournament '" + queued.tournamentId() + "'. Retrying after " + retryAfterMs + "ms.");
                queue.add(queued); // requeue at the back, will retry after backoff
                nextDelay = ticksFromMillis(retryAfterMs);
            } else {
                plugin.getLogger().severe("There was an error attempting to send the webhook! Error: " + ex);
            }
        } catch (NullPointerException ex) {
            plugin.getLogger().severe("There was an error attempting to send the webhook! Error: " + ex);
        }

        processNext(nextDelay);
    }

    private long extractRetryAfterMs(IOException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("429")) {
            return 2000L; // conservative fallback if Retry-After wasn't parseable
        }
        return -1L;
    }

    private long ticksFromMillis(long ms) {
        return Math.max(1L, ms / 50L);
    }
}