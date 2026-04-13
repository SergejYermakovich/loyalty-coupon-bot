/**
 * Dashboard Module
 * Loads and displays business statistics
 */

// Load dashboard data
async function loadDashboard() {
  try {
    const stats = await window.LoyaltyBot.apiCall('/business/stats');
    
    // Update stat cards
    document.getElementById('totalCustomers').textContent = stats.totalCustomers || 0;
    document.getElementById('activeCoupons').textContent = stats.activeCoupons || 0;
    document.getElementById('rewardsClaimed').textContent = stats.rewardsClaimed || 0;
    
    // Load recent activity
    loadRecentActivity(stats.recentActivity);
    
  } catch (error) {
    console.error('Failed to load dashboard', error);
    document.getElementById('recentActivity').innerHTML = `
      <div class="activity-item">
        <div class="activity-icon">❌</div>
        <div class="activity-content">
          <div class="activity-title">Ошибка загрузки</div>
          <div class="activity-time">Попробуйте обновить страницу</div>
        </div>
      </div>
    `;
  }
}

// Load recent activity
function loadRecentActivity(activities = []) {
  const container = document.getElementById('recentActivity');
  
  if (!activities || activities.length === 0) {
    container.innerHTML = `
      <div class="activity-item">
        <div class="activity-icon">📭</div>
        <div class="activity-content">
          <div class="activity-title">Нет активности</div>
          <div class="activity-time">Последние посещения появятся здесь</div>
        </div>
      </div>
    `;
    return;
  }
  
  container.innerHTML = activities.map(activity => `
    <div class="activity-item">
      <div class="activity-icon">${getActivityIcon(activity.type)}</div>
      <div class="activity-content">
        <div class="activity-title">${activity.title}</div>
        <div class="activity-time">${window.LoyaltyBot.formatDate(activity.timestamp)}</div>
      </div>
    </div>
  `).join('');
}

// Get icon for activity type
function getActivityIcon(type) {
  const icons = {
    'stamp_added': '✅',
    'coupon_activated': '🎫',
    'reward_claimed': '🎁',
    'business_created': '🏪',
    'coupon_created': '➕'
  };
  return icons[type] || '📌';
}

// Initialize dashboard
document.addEventListener('DOMContentLoaded', () => {
  loadDashboard();
  
  // Setup BackButton if opened from bot
  tg.BackButton.onClick(() => {
    tg.close();
  });
});

// Refresh on visibility change
document.addEventListener('visibilitychange', () => {
  if (!document.hidden) {
    loadDashboard();
  }
});
